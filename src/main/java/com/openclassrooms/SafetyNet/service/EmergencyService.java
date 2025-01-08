package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.mapper.EmergencyMapper;
import com.openclassrooms.SafetyNet.model.*;
import com.openclassrooms.SafetyNet.repository.FirestationRepository;
import com.openclassrooms.SafetyNet.repository.MedicalRecordRepository;
import com.openclassrooms.SafetyNet.repository.PersonRepository;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;


@Log4j2
@Service
@Data
public class EmergencyService {

    private final PersonRepository personRepository;
    private final FirestationRepository firestationRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final EmergencyMapper emergencyMapper;

    @Autowired
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
        log.info("<service> getPersonCoveredByStationNumber");

        // Récupère les adresses couvertes par la caserne stationNumber
        List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(String.valueOf(stationNumber));

        // Récupère les personnes habitant à ces adresses
        List<Person> persons = new ArrayList<>();
        for (Firestation firestation : firestations) {
            List<Person> p = personRepository.getPersonByAddress(firestation.getAddress());
            persons.addAll(p);
        }

        // Récupère les medical records des personnes
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        for (Person p : persons) {
            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(p.getFirstName(), p.getLastName());

            if (medicalRecord != null) {
                medicalRecords.add(medicalRecord);
            }
        }

        return emergencyMapper.toPersonCoveredByStationDTO(persons, medicalRecords);
    }


    /**
     * Get family (children and adults) information, living at the same address
     *
     * @param address adresse
     * @return liste de ChildrenByAddress
     */
    public FamilyDTO getFamily(String address) {
        log.info("<service> getFamily");

        // Get persons at the same address
        List<Person> persons = personRepository.getPersonByAddress(address);

        // Get MedicalRecords of persons
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        for (Person p : persons) {
            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(p.getFirstName(), p.getLastName());

            if (medicalRecord != null) {
                medicalRecords.add(medicalRecord);
            }
        }

        // Map persons and medicalRecords to Children and Adults
        return emergencyMapper.toFamilyDTO(persons, medicalRecords);
    }

    /**
     * Get phone numbers covered by a fire stations
     *
     * @param stationNumber station number
     * @return HashSet of phone numbers
     */
    public HashSet<String> getPhoneNumbersCoveredByFireStation(String stationNumber) {
        log.info("<service> getPhoneNumbersCoveredByFireStation");

        // HashSet for unique phone numbers
        HashSet<String> phoneNumbers = new HashSet<>();

        // Get Firestations corresponding to the stationNumber
        List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(stationNumber);

        // Get Persons living at the addresses covered by the firestations
        for (Firestation firestation : firestations) {
            List<Person> persons = personRepository.getPersonByAddress(firestation.getAddress());
            for (Person person : persons) {
                phoneNumbers.add(person.getPhone());
            }
        }

        return phoneNumbers;
    }

    /**
     * Get family (persons at same address) with fire station and medical details
     *
     * @param address adresse
     * @return liste de FamilyWithMedicalAndFirestationDTO
     */
    public FamilyWithMedicalAndFirestationDTO getFamilyWithMedicalAndFirestation(String address) {
        log.info("<service> getFamilyWithMedicalAndFirestation");

        // Get Firestation for the address
        // TODO : getFirestationByAddress return one Firesation, not a list (to be fixed?)
        Firestation firestation = firestationRepository.getFirestationByAddress(address);
        if (firestation == null) {
            return new FamilyWithMedicalAndFirestationDTO();
        }

        // Get persons covered by the firestation
        List<Person> persons = personRepository.getPersonByAddress(address);

        // Get MedicalRecords of persons
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        for (Person person : persons) {
            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
            if (medicalRecord != null) {
                medicalRecords.add(medicalRecord);
            }
        }

        // Map persons, medicalRecords and firestation to FamilyWithMedicalAndFirestationDTO
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
            List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(String.valueOf(stationNumber));
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
        List<PersonWithMedicalRecord> personWithMedicalRecords = emergencyMapper.toPersonWithMedicalRecord(finalPersonList, finalMedicalRecordList);

        // Group by address
        HashMap<String, List<PersonWithMedicalAndPhoneDTO>> personGroupedByAddress = new HashMap<>();

        for (PersonWithMedicalRecord personWithMedicalRecord : personWithMedicalRecords) {
            // Map to PersonWithMedicalAndPhoneDTO
            PersonWithMedicalAndPhoneDTO personWithMedicalAndPhoneDTO = emergencyMapper.toPersonWithMedicalAndPhone(personWithMedicalRecord);
            // Group by address
            personGroupedByAddress.computeIfAbsent(personWithMedicalRecord.getAddress(), k -> new ArrayList<>()).add(personWithMedicalAndPhoneDTO);
        }
        familyDTO.setAddress(personGroupedByAddress);

        return familyDTO;
    }


    /**
     * Retourne une liste de personnes avec les détails médicaux et l'email
     *
     * @param lastName nom de famille
     * @return liste de PersonMedicalDetailsWithEmail
     */
    public List<PersonMedicalDetailsWithEmail> getPersonMedicalDetailsWithEmail(String lastName) {
        log.info("<service> getPersonMedicalDetailsWithEmail");
        List<PersonMedicalDetailsWithEmail> personReturned = new ArrayList<>();

        List<Person> personList = personRepository.getPersons();
        for (Person person : personList) {
            if (person.getLastName().equals(lastName)) {
                MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
                if (medicalRecord != null) {
                    PersonMedicalDetailsWithEmail personMedicalDetailsWithEmail = new PersonMedicalDetailsWithEmail();
                    personMedicalDetailsWithEmail.setFirstName(person.getFirstName());
                    personMedicalDetailsWithEmail.setLastName(person.getLastName());
                    personMedicalDetailsWithEmail.setEmail(person.getEmail());
                    personMedicalDetailsWithEmail.setAge(calculateAge(medicalRecord.getBirthdate()));
                    personMedicalDetailsWithEmail.setMedications(medicalRecord.getMedications());
                    personMedicalDetailsWithEmail.setAllergies(medicalRecord.getAllergies());

                    personReturned.add(personMedicalDetailsWithEmail);
                }
            }
        }
        return personReturned;
    }


    /**
     * Get email of persons living in the city
     *
     * @param city name of the city
     * @return HashSet of email
     */
    public HashSet<String> getPersonEmailByCity(String city) {
        log.info("<service> getPersonEmailByCity");
        HashSet<String> emailList = new HashSet<>();

        List<Person> personList = personRepository.getPersons();
        for (Person person : personList) {
            if (person.getCity().equals(city)) {
                emailList.add(person.getEmail());
            }
        }
        return emailList;
    }

    /**
     * Calcule l'âge d'une personne
     *
     * @param birthdate date de naissance
     * @return âge
     */
    private int calculateAge(Date birthdate) {
        // Converti la date de naissance en LocalDate, pour cela :
        // Converti la date en Instant (temps en millisecondes depuis le 1er janvier 1970)
        // Converti l'Instant en ZoneDateTime (date et heure) en utilisant le fuseau horaire du système
        LocalDate birthDateLocal = birthdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        return Period.between(birthDateLocal, today).getYears();
    }


}
