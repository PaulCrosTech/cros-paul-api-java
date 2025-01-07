package com.openclassrooms.SafetyNet.service;

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

    @Autowired
    public EmergencyService(PersonRepository personRepository,
                            FirestationRepository firestationRepository,
                            MedicalRecordRepository medicalRecordRepository) {
        log.info("<constructor> EmergencyService");
        this.personRepository = personRepository;
        this.firestationRepository = firestationRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }


    /**
     * Retourne une liste de personnes couvertes par la caserne stationNumber
     *
     * @param stationNumber numéro de la caserne
     * @return liste de PersonCoveredByStation
     */
    public PersonCoveredByStation getPersonCoveredByStationNumber(int stationNumber) {
        log.info("<service> getPersonCoveredByStationNumber");

        // Récupère les adresses couvertes par la caserne stationNumber
        List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(String.valueOf(stationNumber));

        // Récupère les personnes habitant à ces adresses
        List<Person> personsCovered = new ArrayList<>();
        for (Firestation firestation : firestations) {
            List<Person> p = personRepository.getPersonByAddress(firestation.getAddress());
            personsCovered.addAll(p);
        }

        // Calcul : nombre d'enfants et adultes
        int nbChildrens = 0;
        int nbAdults = 0;
        for (Person p : personsCovered) {
            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(p.getFirstName(), p.getLastName());

            if (medicalRecord != null) {
                int age = calculateAge(medicalRecord.getBirthdate());
                if (age <= 18) {
                    nbChildrens++;
                    log.info("<service> getPersonCoveredByStationNumber - medicalRecord Children : {}", medicalRecord);
                } else {
                    nbAdults++;
                    log.info("<service> getPersonCoveredByStationNumber - medicalRecord Adult : {}", medicalRecord);
                }
            }
        }

        // Filtre les données pour ne garder que les informations nécessaires
        List<PersonByFirestationDTO> personFiltered = filterPersonDTO(personsCovered);

        // Crée un objet PersonCoveredByStation
        PersonCoveredByStation personCoveredByStation = new PersonCoveredByStation();
        personCoveredByStation.setNbChildrens(nbChildrens);
        personCoveredByStation.setNbAdults(nbAdults);
        personCoveredByStation.setPersonsCovered(personFiltered);

        return personCoveredByStation;
    }


    /**
     * Retourne une liste d'enfants avec les informations de leurs parents
     *
     * @param address adresse
     * @return liste de ChildrenByAddress
     */
    public PersonAtSameAddress getPersonAtSameAddress(String address) {

        // Récupère les personnes habitant à cette adresse
        List<Person> persons = personRepository.getPersonByAddress(address);

        PersonAtSameAddress personAtSameAddress = new PersonAtSameAddress();
        List<Children> childrens = new ArrayList<>();
        List<Adult> adults = new ArrayList<>();

        // Pour chaque personne, récupère son âge et le classe dans la liste enfants ou adultes
        for (Person p : persons) {
            MedicalRecord medicalRecord = medicalRecordRepository.getMedicalRecordByFirstNameAndLastName(p.getFirstName(), p.getLastName());
            if (medicalRecord != null) {
                int age = calculateAge(medicalRecord.getBirthdate());
                if (age <= 18) {
                    Children c = new Children();
                    c.setAge(age);
                    c.setFirstName(p.getFirstName());
                    c.setLastName(p.getLastName());
                    childrens.add(c);
                } else {
                    Adult a = new Adult();
                    a.setFirstName(p.getFirstName());
                    a.setLastName(p.getLastName());
                    adults.add(a);
                }
            }
        }

        // Finalise l'objet AddressOccupants
        personAtSameAddress.setChildrens(childrens);
        personAtSameAddress.setAdults(adults);

        return personAtSameAddress;
    }

    /**
     * Retourne une liste de numéros de téléphone des personnes couvertes par la caserne stationNumber
     *
     * @param stationNumber numéro de la caserne
     * @return liste de numéros de téléphone
     */
    public HashSet<String> getPhoneNumbersCoveredByFireStation(String stationNumber) {
        log.info("<service> getPhoneAlert");

        HashSet<String> phoneNumbers = new HashSet<>();

        // Récupère les personnes couvertes par la caserne firestation
        List<Firestation> firestations = firestationRepository.getFirestationByStationNumber(stationNumber);

        // Récupère les personnes habitant à ces adresses
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

    /**
     * Filtre les données pour ne garder que les informations nécessaires
     *
     * @param persons liste de personnes
     * @return liste de PersonByFirestationDTO
     */
    private List<PersonByFirestationDTO> filterPersonDTO(List<Person> persons) {
        List<PersonByFirestationDTO> personFiltered = new ArrayList<>();
        for (Person person : persons) {
            PersonByFirestationDTO p = new PersonByFirestationDTO();
            p.setFirstName(person.getFirstName());
            p.setLastName(person.getLastName());
            p.setPhone(person.getPhone());
            p.setAddress(person.getAddress());
            personFiltered.add(p);
        }
        return personFiltered;
    }


}
