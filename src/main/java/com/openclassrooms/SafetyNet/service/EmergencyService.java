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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    public AddressOccupants getOccupantsByAddress(String address) {

        // Récupère les personnes habitant à cette adresse
        List<Person> persons = personRepository.getPersonByAddress(address);

        AddressOccupants addressOccupants = new AddressOccupants();
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
        addressOccupants.setChildrens(childrens);
        addressOccupants.setAdults(adults);

        return addressOccupants;
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
