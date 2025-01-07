package com.openclassrooms.SafetyNet.mapper;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class PersonMapper {


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
     * Convert a Person to a PersonAtSameAddressDTO
     *
     * @param persons        list of persons
     * @param medicalRecords list of medical records
     * @return the converted PersonAtSameAddressDTO
     */
    public PersonAtSameAddressDTO toPersonAtSameAddressDTO(
            List<Person> persons,
            List<MedicalRecord> medicalRecords
    ) {
        PersonAtSameAddressDTO personAtSameAddressDTO = new PersonAtSameAddressDTO();

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
        List<ChildrenDTO> childrens = personWithMedicalRecords.stream()
                .filter(personWithMedicalRecord -> !personWithMedicalRecord.getIsAdult())
                .map(personWithMedicalRecord -> new ChildrenDTO(
                        personWithMedicalRecord.getFirstName(),
                        personWithMedicalRecord.getLastName(),
                        personWithMedicalRecord.getAge()
                ))
                .toList();
        
        personAtSameAddressDTO.setAdults(adults);
        personAtSameAddressDTO.setChildrens(childrens);

        return personAtSameAddressDTO;

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
