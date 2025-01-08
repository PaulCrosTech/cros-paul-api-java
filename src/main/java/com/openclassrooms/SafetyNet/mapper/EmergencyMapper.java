package com.openclassrooms.SafetyNet.mapper;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class EmergencyMapper {


    /**
     * Convert a Person to a PersonByFirestationDTO
     *
     * @param person the person to convert
     * @return the converted PersonByFirestationDTO
     */
    public PersonByFirestationDTO toPersonByFirestationDTO(Person person) {
        return new PersonByFirestationDTO(
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getPhone()
        );
    }


    /**
     * Convert a Person to a PersonAtSameAddressDTO
     *
     * @param persons        list of persons
     * @param medicalRecords list of medical records
     * @return the converted PersonAtSameAddressDTO
     */
    public PersonCoveredByStationDTO toPersonCoveredByStationDTO(List<Person> persons, List<MedicalRecord> medicalRecords) {
        // Associate Person with MedicalRecord
        List<PersonWithMedicalRecord> personWithMedicalRecords = toPersonWithMedicalRecord(persons, medicalRecords);

        PersonCoveredByStationDTO personCoveredByStationDTO = new PersonCoveredByStationDTO();

        // Count Adult & Children
        int nbAdults = 0;
        int nbChildrens = 0;
        for (PersonWithMedicalRecord person : personWithMedicalRecords) {
            if (person.getIsAdult()) {
                nbAdults++;
            } else {
                nbChildrens++;
            }
        }
        personCoveredByStationDTO.setNbAdults(nbAdults);
        personCoveredByStationDTO.setNbChildrens(nbChildrens);

        // Map PersonWithMedicalRecord to PersonDTO
        List<PersonBasicDetailsDTO> personBasicDetailsDTO = personWithMedicalRecords.stream()
                .map(personWithMedicalRecord -> new PersonBasicDetailsDTO(
                        personWithMedicalRecord.getFirstName(),
                        personWithMedicalRecord.getLastName(),
                        personWithMedicalRecord.getAddress(),
                        personWithMedicalRecord.getPhone()
                ))
                .toList();

        personCoveredByStationDTO.setPersons(personBasicDetailsDTO);

        return personCoveredByStationDTO;

    }

    /**
     * Convert a Person (split info adults and children) to a FamilyDTO
     *
     * @param persons        list of persons
     * @param medicalRecords list of medical records
     * @return the converted PersonAtSameAddressDTO
     */
    public FamilyDTO toFamilyDTO(
            List<Person> persons,
            List<MedicalRecord> medicalRecords
    ) {
        FamilyDTO familyDTO = new FamilyDTO();

        // Associate Person with MedicalRecord
        List<PersonWithMedicalRecord> personWithMedicalRecords = toPersonWithMedicalRecord(persons, medicalRecords);

        // Map PersonWithMedicalRecord to AdultDTO
        List<AdultDTO> adults = personWithMedicalRecords.stream()
                .filter(PersonWithMedicalRecord::getIsAdult)
                .map(personWithMedicalRecord -> new AdultDTO(
                        personWithMedicalRecord.getFirstName(),
                        personWithMedicalRecord.getLastName()
                ))
                .toList();

        // Map PersonWithMedicalRecord to ChrildrenDTO
        List<ChildrenDTO> children = personWithMedicalRecords.stream()
                .filter(personWithMedicalRecord -> !personWithMedicalRecord.getIsAdult())
                .map(personWithMedicalRecord -> new ChildrenDTO(
                        personWithMedicalRecord.getFirstName(),
                        personWithMedicalRecord.getLastName(),
                        personWithMedicalRecord.getAge()
                ))
                .toList();

        familyDTO.setAdults(adults);
        familyDTO.setChildren(children);

        return familyDTO;

    }


    /**
     * Convert a Person, MedicalRecord, Firestation to a FamilyWithMedicalAndFirestationDTO
     *
     * @param persons        list of persons
     * @param medicalRecords list of medical records
     * @param firestation    firestation
     * @return the converted FamilyWithMedicalAndFirestationDTO
     */
    public FamilyWithMedicalAndFirestationDTO toFamilyWithMedicalAndFirestationDTO(List<Person> persons,
                                                                                   List<MedicalRecord> medicalRecords,
                                                                                   Firestation firestation) {

        FamilyWithMedicalAndFirestationDTO familyDTO = new FamilyWithMedicalAndFirestationDTO();
        familyDTO.setStation(firestation.getStation());

        // Associate Person with MedicalRecord
        List<PersonWithMedicalRecord> personWithMedicalRecords = toPersonWithMedicalRecord(persons, medicalRecords);

        // Map PersonWithMedicalRecord to PersonMedicalDetails
        List<PersonMedicalDetails> personMedicalDetails = personWithMedicalRecords.stream()
                .map(personWithMedicalRecord -> new PersonMedicalDetails(
                        personWithMedicalRecord.getFirstName(),
                        personWithMedicalRecord.getLastName(),
                        personWithMedicalRecord.getMedications(),
                        personWithMedicalRecord.getAllergies(),
                        personWithMedicalRecord.getAge()
                ))
                .toList();

        familyDTO.setPersonMedicalDetails(personMedicalDetails);
        return familyDTO;
    }

    /**
     * Associate Person with MedicalRecord
     *
     * @param persons        list of persons
     * @param medicalRecords list of medical records
     * @return list of persons with medical records
     */
    private List<PersonWithMedicalRecord> toPersonWithMedicalRecord(List<Person> persons, List<MedicalRecord> medicalRecords) {

        List<PersonWithMedicalRecord> personWithMedicalRecords = persons.stream()
                .map(person -> {
                    MedicalRecord medicalRecord = medicalRecords.stream()
                            .filter(record -> record.getFirstName().equals(person.getFirstName()) && record.getLastName().equals(person.getLastName()))
                            .findFirst()
                            .orElse(null);

                    PersonWithMedicalRecord personWithMedicalRecord = new PersonWithMedicalRecord();
                    personWithMedicalRecord.setFirstName(person.getFirstName());
                    personWithMedicalRecord.setLastName(person.getLastName());
                    personWithMedicalRecord.setAddress(person.getAddress());
                    personWithMedicalRecord.setCity(person.getCity());
                    personWithMedicalRecord.setZip(person.getZip());
                    personWithMedicalRecord.setPhone(person.getPhone());
                    personWithMedicalRecord.setEmail(person.getEmail());

                    if (medicalRecord != null) {
                        personWithMedicalRecord.setAge(calculateAge(medicalRecord.getBirthdate()));
                        personWithMedicalRecord.setIsAdult(personWithMedicalRecord.getAge() >= 18);
                        personWithMedicalRecord.setMedications(medicalRecord.getMedications());
                        personWithMedicalRecord.setAllergies(medicalRecord.getAllergies());
                    }

                    return personWithMedicalRecord;
                })
                .toList();

        return personWithMedicalRecords;

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
