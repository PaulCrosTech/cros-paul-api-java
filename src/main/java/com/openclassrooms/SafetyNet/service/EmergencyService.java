package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.exceptions.NotFoundException;
import com.openclassrooms.SafetyNet.mapper.EmergencyMapper;
import com.openclassrooms.SafetyNet.model.*;
import com.openclassrooms.SafetyNet.repository.FirestationRepository;
import com.openclassrooms.SafetyNet.repository.MedicalRecordRepository;
import com.openclassrooms.SafetyNet.repository.PersonRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * EmergencyService Class
 */
@Log4j2
@Service
@Data
public class EmergencyService {

    private final PersonRepository personRepository;
    private final FirestationRepository firestationRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final EmergencyMapper emergencyMapper;

    /**
     * Constructeur
     *
     * @param personRepository        person repository
     * @param firestationRepository   firestation repository
     * @param medicalRecordRepository medical record repository
     * @param emergencyMapper         emergency mapper
     */
    public EmergencyService(PersonRepository personRepository,
                            FirestationRepository firestationRepository,
                            MedicalRecordRepository medicalRecordRepository,
                            EmergencyMapper emergencyMapper) {
        log.info("<constructor> EmergencyService");
        this.personRepository = personRepository;
        this.firestationRepository = firestationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.emergencyMapper = emergencyMapper;
    }

    /**
     * Retourne une liste de personnes couvertes par la caserne stationNumber
     *
     * @param stationNumber numéro de la caserne
     * @return liste de PersonCoveredByStation
     */
    public PersonCoveredByStationDTO getPersonCoveredByStationNumber(int stationNumber) {
        // Récupère les adresses couvertes par la caserne stationNumber
        List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(stationNumber);
        if (firestations.isEmpty()) {
            throw new NotFoundException("No firestation found for station number " + stationNumber);
        }

        Map<Person, String> personWithBirthdate = new LinkedHashMap<>();

        // Récupère les personnes vivant aux adresses couvertes par les casernes
        for (Firestation firestation : firestations) {
            List<Person> persons = personRepository.getPersonByAddress(firestation.getAddress());

            // Récupère les dates de naissances des personnes
            for (Person p : persons) {
                String birthdate = medicalRecordRepository.getBirthdateByFirstNameAndLastName(p.getFirstName(), p.getLastName());
                if (birthdate != null) {
                    personWithBirthdate.put(p, birthdate);
                }
            }
        }

        log.info("{} persons found", personWithBirthdate.size());
        return emergencyMapper.toPersonCoveredByStationDTO(personWithBirthdate);
    }


    /**
     * Return list of children living at the address, with other members of the house
     *
     * @param address address
     * @return List of HouseChildrenDTO objects
     */
    public List<HouseChildrenDTO> getHouseChildren(String address) {
        // Get persons at the same address
        List<Person> persons = personRepository.getPersonByAddress(address);


        // Map person with birthdate
        Map<Person, String> personWithBirthdate = new LinkedHashMap<>();

        for (Person p : persons) {
            String birthdate = medicalRecordRepository.getBirthdateByFirstNameAndLastName(p.getFirstName(), p.getLastName());
            if (birthdate != null) {
                personWithBirthdate.put(p, birthdate);
            }
        }


        // Map persons (with birthdate) to HouseChildrenDTO
        log.info("{} persons found", personWithBirthdate.size());
        return emergencyMapper.toHouseChildrenDTO(personWithBirthdate);

    }

    /**
     * Get phone numbers covered by a fire stations
     *
     * @param stationNumber station number
     * @return HashSet of phone numbers
     */
    public HashSet<String> getPhoneNumbersCoveredByFireStation(int stationNumber) {
        // HashSet for unique phone numbers
        HashSet<String> phoneNumbers = new HashSet<>();

        // Get Firestations corresponding to the stationNumber
        List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(stationNumber);
        if (firestations.isEmpty()) {
            throw new NotFoundException("No firestation found for station number " + stationNumber);
        }

        // Get Persons living at the addresses covered by the firestations
        for (Firestation firestation : firestations) {
            List<Person> persons = personRepository.getPersonByAddress(firestation.getAddress());
            for (Person person : persons) {
                phoneNumbers.add(person.getPhone());
            }
        }

        log.info("{} phone numbers found", phoneNumbers.size());
        return phoneNumbers;
    }

    /**
     * Get family (persons at same address) with fire station and medical details
     *
     * @param address adresse
     * @return liste de FamilyWithMedicalAndFirestationDTO
     */
    public FamilyWithMedicalAndFirestationDTO getFamilyWithMedicalAndFirestation(String address) {

        // Get Firestation for the address
        Firestation firestation = firestationRepository.getFirestationByAddress(address);
        if (firestation == null) {
            return new FamilyWithMedicalAndFirestationDTO();
        }

        // Get persons covered by the firestation
        List<Person> persons = personRepository.getPersonByAddress(address);

        // Get MedicalRecords of persons
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        for (Person person : persons) {
            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(
                    person.getFirstName(),
                    person.getLastName());
            if (medicalRecord != null) {
                medicalRecords.add(medicalRecord);
            }
        }

        // Map persons, medicalRecords and firestation to FamilyWithMedicalAndFirestationDTO
        log.info("{} persons found", persons.size());
        return emergencyMapper.toFamilyWithMedicalAndFirestationDTO(persons, medicalRecords, firestation);
    }


    /**
     * Get family (persons at same address) with medical details, grouped by address
     *
     * @param stationNumbers List of station numbers
     * @return FamilyWithMedicalGroupedByAddressDTO object
     */
    public FamilyWithMedicalGroupedByAddressDTO getFamilyWithMedicalGroupedByAddress(List<Integer> stationNumbers) {

        FamilyWithMedicalGroupedByAddressDTO familyDTO = new FamilyWithMedicalGroupedByAddressDTO();

        // Get unique addresses of Firestations corresponding to the stationNumbers
        Set<String> addresses = new HashSet<>();

        for (Integer stationNumber : stationNumbers) {
            List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(stationNumber);
            for (Firestation f : firestations) {
                addresses.add(f.getAddress());
            }
        }

        // Get Persons, with medical record, living at the addresses
        List<Person> finalPersonList = new ArrayList<>();
        List<MedicalRecord> finalMedicalRecordList = new ArrayList<>();

        for (String address : addresses) {
            List<Person> tempPersonList = personRepository.getPersonByAddress(address);
            finalPersonList.addAll(tempPersonList);

            // Get MedicalRecords of persons
            for (Person person : tempPersonList) {
                MedicalRecord tempMedicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
                if (tempMedicalRecord != null) {
                    finalMedicalRecordList.add(tempMedicalRecord);
                }
            }
        }

        // Associate Person with MedicalRecord
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = emergencyMapper.toPersonWithMedicalRecord(finalPersonList, finalMedicalRecordList);

        // Group by address
        HashMap<String, List<PersonWithMedicalAndPhoneDTO>> personGroupedByAddress = new HashMap<>();

        for (PersonWithMedicalRecordDTO personWithMedicalRecordDTO : personWithMedicalRecordDTOS) {
            // Map to PersonWithMedicalAndPhoneDTO
            PersonWithMedicalAndPhoneDTO personWithMedicalAndPhoneDTO = emergencyMapper.toPersonWithMedicalAndPhone(personWithMedicalRecordDTO);
            // Group by address
            personGroupedByAddress.computeIfAbsent(
                    personWithMedicalRecordDTO.getAddress(), k -> new ArrayList<>()).add(personWithMedicalAndPhoneDTO
            );
        }
        familyDTO.setAddress(personGroupedByAddress);

        log.info("{} persons found", finalPersonList.size());
        return familyDTO;
    }


    /**
     * Get person by last name, with medical details and email
     *
     * @param lastName last name of the persons
     * @return List of PersonWithMedicalAndEmailDTO objects
     */
    public List<PersonWithMedicalAndEmailDTO> getPersonMedicalWithEmail(String lastName) {
        // Get all persons with lat name equals to lastName
        List<Person> personsList = personRepository.getPersonByLastName(lastName);
        if (personsList.isEmpty()) {
            throw new NotFoundException("No person found with last name " + lastName);
        }

        // Get medical record of persons
        List<MedicalRecord> medicalRecordsList = new ArrayList<>();
        for (Person person : personsList) {
            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(
                    person.getFirstName(), person.getLastName());
            if (medicalRecord != null) {
                medicalRecordsList.add(medicalRecord);
            }
        }

        // Associate Person with MedicalRecord
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = emergencyMapper.toPersonWithMedicalRecord(personsList, medicalRecordsList);

        // Map to PersonWithMedicalAndEmailDTO
        List<PersonWithMedicalAndEmailDTO> personWithMedicalAndEmailDTOS = new ArrayList<>();
        for (PersonWithMedicalRecordDTO p : personWithMedicalRecordDTOS) {
            PersonWithMedicalAndEmailDTO personWithMedicalAndEmailDTO = emergencyMapper.toPersonWithMedicalAndEmailDTO(p);
            personWithMedicalAndEmailDTOS.add(personWithMedicalAndEmailDTO);
        }

        log.info("{} persons found", personWithMedicalAndEmailDTOS.size());
        return personWithMedicalAndEmailDTOS;
    }


    /**
     * Get email of persons living in the city
     *
     * @param city name of the city
     * @return HashSet of email
     */
    public HashSet<String> getPersonEmailByCity(String city) {

        HashSet<String> emailList = new HashSet<>();

        List<Person> personList = personRepository.getPersons();
        for (Person person : personList) {
            if (person.getCity().equals(city)) {
                emailList.add(person.getEmail());
            }
        }
        log.info("{} emails found", emailList.size());
        return emailList;
    }

}
