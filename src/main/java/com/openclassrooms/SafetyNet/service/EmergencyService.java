package com.openclassrooms.SafetyNet.service;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.mapper.PersonMapper;
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
    private final PersonMapper personMapper;

    @Autowired
    public EmergencyService(PersonRepository personRepository,
                            FirestationRepository firestationRepository,
                            MedicalRecordRepository medicalRecordRepository,
                            PersonMapper personMapper) {
        log.info("<constructor> EmergencyService");
        this.personRepository = personRepository;
        this.firestationRepository = firestationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.personMapper = personMapper;
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

        return personMapper.toPersonCoveredByStationDTO(persons, medicalRecords);
    }


    /**
     * Retourne une liste d'enfants avec les informations de leurs parents
     *
     * @param address adresse
     * @return liste de ChildrenByAddress
     */
    public PersonAtSameAddressDTO getPersonAtSameAddress(String address) {

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
        return personMapper.toPersonAtSameAddressDTO(persons, medicalRecords);
    }

    /**
     * Get phone numbers covered by a fire stations
     *
     * @param stationNumber station number
     * @return HashSet of phone numbers
     */
    public HashSet<String> getPhoneNumbersCoveredByFireStation(String stationNumber) {
        log.info("<service> getPhoneAlert");

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
     * Retourne une liste de personnes habitant à la même adresse avec les détails médicaux et la caserne de rattachement
     *
     * @param address adresse
     * @return liste de PersonAtSameAddressWithMedicalDetailsAndFirestation
     */
    public PersonAtSameAddressWithMedicalDetailsAndFirestation getPersonsAtSameAddressWithMedicalDetailsAndFirestation(String address) {
        log.info("<service> getPersonsAtSameAddressWithFirestation");

        PersonAtSameAddressWithMedicalDetailsAndFirestation personsReturned = new PersonAtSameAddressWithMedicalDetailsAndFirestation();

        // Récupère la liste des casernes couvrant cette adresse
        Firestation firestation = firestationRepository.getFirestationByAddress(address);
        if (firestation == null) {
            return personsReturned;
        }
        personsReturned.setStation(firestation.getStation());
        List<PersonMedicalDetails> personMedicalDetailsList = new ArrayList<>();

        // Récupère les personnes habitant à cette adresse
        List<Person> personsAtSameAddress = personRepository.getPersonByAddress(address);
        for (Person person : personsAtSameAddress) {

            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
            if (medicalRecord != null) {

                PersonMedicalDetails personMedicalDetails = new PersonMedicalDetails();
                personMedicalDetails.setFirstName(person.getFirstName());
                personMedicalDetails.setMedications(medicalRecord.getMedications());
                personMedicalDetails.setAllergies(medicalRecord.getAllergies());
                personMedicalDetails.setAge(calculateAge(medicalRecord.getBirthdate()));

                personMedicalDetailsList.add(personMedicalDetails);
            }
        }
        personsReturned.setPersonMedicalDetails(personMedicalDetailsList);

        return personsReturned;
    }


    /**
     * Retourne une liste de personnes, avec les détails médicaux, regroupés par address
     *
     * @param stationNumbers liste de numéros de casernes
     * @return liste de PersonWithMedicalDetailsGroupedByAddress
     */
    public PersonWithMedicalDetailsGroupedByAddress getPersonWithMedicalDetailsGroupedByAddress(List<Integer> stationNumbers) {
        PersonWithMedicalDetailsGroupedByAddress personReturned = new PersonWithMedicalDetailsGroupedByAddress();
        HashMap<String, List<PersonMedicalDetailsWithPhone>> addressReturned = new HashMap<>();

        // Récupère les Firestations pour chaque stationNumber
        for (Integer i : stationNumbers) {
            List<Firestation> addressesList = firestationRepository.getFirestationByStationNumber(String.valueOf(i));

            // Récupère les addresses couvertes par la caserne stationNumber
            for (Firestation address : addressesList) {
                List<Person> personList = personRepository.getPersonByAddress(address.getAddress());
                List<PersonMedicalDetailsWithPhone> medicalRecordReturned = new ArrayList<>();

                // Récupère les informations médicales pour chaque personne
                for (Person person : personList) {
                    MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());

                    if (medicalRecord != null) {
                        PersonMedicalDetailsWithPhone personMedicalDetailsWithPhone = new PersonMedicalDetailsWithPhone();
                        personMedicalDetailsWithPhone.setFirstName(person.getFirstName());
                        personMedicalDetailsWithPhone.setLastName(person.getLastName());
                        personMedicalDetailsWithPhone.setPhone(person.getPhone());
                        personMedicalDetailsWithPhone.setAge(calculateAge(medicalRecord.getBirthdate()));
                        personMedicalDetailsWithPhone.setMedications(medicalRecord.getMedications());
                        personMedicalDetailsWithPhone.setAllergies(medicalRecord.getAllergies());

                        // Ajoute le medicalRecord à la liste temporaire, groupée par adresse
                        medicalRecordReturned.add(personMedicalDetailsWithPhone);
                    }
                }

                // Affecte la liste de MedicalRecord à l'adresse
                addressReturned.put(address.getAddress(), medicalRecordReturned);
            }
        }
        // Affecte la liste d'adresse à l'objet retourné
        personReturned.setAddress(addressReturned);

        return personReturned;
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
