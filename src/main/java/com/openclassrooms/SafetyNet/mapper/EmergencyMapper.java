package com.openclassrooms.SafetyNet.mapper;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Log4j2
@Component
public class EmergencyMapper {

    /**
     * Convert a Person to a PersonAtSameAddressDTO
     *
     * @param persons        list of persons
     * @param medicalRecords list of medical records
     * @return the converted PersonAtSameAddressDTO
     */
    public PersonCoveredByStationDTO toPersonCoveredByStationDTO(List<Person> persons, List<MedicalRecord> medicalRecords) {
        // Associate Person with MedicalRecord
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = toPersonWithMedicalRecord(persons, medicalRecords);

        PersonCoveredByStationDTO personCoveredByStationDTO = new PersonCoveredByStationDTO();

        // Count Adult & Children
        int nbAdults = 0;
        int nbChildren = 0;
        for (PersonWithMedicalRecordDTO person : personWithMedicalRecordDTOS) {
            if (person.getIsAdult()) {
                nbAdults++;
            } else {
                nbChildren++;
            }
        }

        personCoveredByStationDTO.setNbAdults(nbAdults);
        personCoveredByStationDTO.setNbChildren(nbChildren);

        // Map PersonWithMedicalRecord to PersonDTO
        List<PersonBasicDetailsDTO> personBasicDetailsDTO = personWithMedicalRecordDTOS.stream()
                .map(personWithMedicalRecordDTO -> new PersonBasicDetailsDTO(
                        personWithMedicalRecordDTO.getFirstName(),
                        personWithMedicalRecordDTO.getLastName(),
                        personWithMedicalRecordDTO.getAddress(),
                        personWithMedicalRecordDTO.getPhone()
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
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = toPersonWithMedicalRecord(persons, medicalRecords);

        // Map PersonWithMedicalRecord to AdultDTO
        List<AdultDTO> adults = personWithMedicalRecordDTOS.stream()
                .filter(PersonWithMedicalRecordDTO::getIsAdult)
                .map(personWithMedicalRecordDTO -> new AdultDTO(
                        personWithMedicalRecordDTO.getFirstName(),
                        personWithMedicalRecordDTO.getLastName()
                ))
                .toList();

        // Map PersonWithMedicalRecord to ChrildrenDTO
        List<ChildrenDTO> children = personWithMedicalRecordDTOS.stream()
                .filter(personWithMedicalRecordDTO -> !personWithMedicalRecordDTO.getIsAdult())
                .map(personWithMedicalRecordDTO -> new ChildrenDTO(
                        personWithMedicalRecordDTO.getFirstName(),
                        personWithMedicalRecordDTO.getLastName(),
                        personWithMedicalRecordDTO.getAge()
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
        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = toPersonWithMedicalRecord(persons, medicalRecords);

        // Map PersonWithMedicalRecord to PersonMedicalDetails
        List<PersonMedicalDetailsDTO> personMedicalDetailDTOS = personWithMedicalRecordDTOS.stream()
                .map(personWithMedicalRecordDTO -> new PersonMedicalDetailsDTO(
                        personWithMedicalRecordDTO.getFirstName(),
                        personWithMedicalRecordDTO.getLastName(),
                        personWithMedicalRecordDTO.getMedications(),
                        personWithMedicalRecordDTO.getAllergies(),
                        personWithMedicalRecordDTO.getAge()
                ))
                .toList();

        familyDTO.setPersonMedicalDetailDTOS(personMedicalDetailDTOS);
        return familyDTO;
    }


    /**
     * Convert a PersonWithMedicalRecord to a PersonWithMedicalAndPhoneDTO
     *
     * @param person the person to convert
     * @return the converted PersonWithMedicalAndPhoneDTO
     */
    public PersonWithMedicalAndPhoneDTO toPersonWithMedicalAndPhone(PersonWithMedicalRecordDTO person) {
        PersonWithMedicalAndPhoneDTO personWithMedicalAndPhoneDTO = new PersonWithMedicalAndPhoneDTO();
        personWithMedicalAndPhoneDTO.setFirstName(person.getFirstName());
        personWithMedicalAndPhoneDTO.setLastName(person.getLastName());
        personWithMedicalAndPhoneDTO.setMedications(person.getMedications());
        personWithMedicalAndPhoneDTO.setAllergies(person.getAllergies());
        personWithMedicalAndPhoneDTO.setAge(person.getAge());
        personWithMedicalAndPhoneDTO.setPhone(person.getPhone());
        return personWithMedicalAndPhoneDTO;
    }

    /**
     * Convert a PersonWithMedicalRecord to a PersonWithMedicalAndEmailDTO
     *
     * @param person the person to convert
     * @return the converted PersonWithMedicalAndEmailDTO
     */
    public PersonWithMedicalAndEmailDTO toPersonWithMedicalAndEmailDTO(PersonWithMedicalRecordDTO person) {
        PersonWithMedicalAndEmailDTO personWithMedicalAndEmailDTO = new PersonWithMedicalAndEmailDTO();
        personWithMedicalAndEmailDTO.setFirstName(person.getFirstName());
        personWithMedicalAndEmailDTO.setLastName(person.getLastName());
        personWithMedicalAndEmailDTO.setMedications(person.getMedications());
        personWithMedicalAndEmailDTO.setAllergies(person.getAllergies());
        personWithMedicalAndEmailDTO.setAge(person.getAge());
        personWithMedicalAndEmailDTO.setEmail(person.getEmail());
        return personWithMedicalAndEmailDTO;
    }

    /**
     * Associate Person with MedicalRecord
     *
     * @param persons        list of persons
     * @param medicalRecords list of medical records
     * @return list of persons with medical records
     */
    public List<PersonWithMedicalRecordDTO> toPersonWithMedicalRecord(List<Person> persons, List<MedicalRecord> medicalRecords) {

        List<PersonWithMedicalRecordDTO> personWithMedicalRecordDTOS = persons.stream()
                .map(person -> {
                    MedicalRecord medicalRecord = medicalRecords.stream()
                            .filter(record -> record.getFirstName().equals(person.getFirstName()) && record.getLastName().equals(person.getLastName()))
                            .findFirst()
                            .orElse(null);

                    PersonWithMedicalRecordDTO personWithMedicalRecordDTO = new PersonWithMedicalRecordDTO();
                    personWithMedicalRecordDTO.setFirstName(person.getFirstName());
                    personWithMedicalRecordDTO.setLastName(person.getLastName());
                    personWithMedicalRecordDTO.setAddress(person.getAddress());
                    personWithMedicalRecordDTO.setCity(person.getCity());
                    personWithMedicalRecordDTO.setZip(person.getZip());
                    personWithMedicalRecordDTO.setPhone(person.getPhone());
                    personWithMedicalRecordDTO.setEmail(person.getEmail());

                    if (medicalRecord != null) {
                        personWithMedicalRecordDTO.setAge(calculateAge(medicalRecord.getBirthdate()));
                        personWithMedicalRecordDTO.setIsAdult(personWithMedicalRecordDTO.getAge() >= 18);
                        personWithMedicalRecordDTO.setMedications(medicalRecord.getMedications());
                        personWithMedicalRecordDTO.setAllergies(medicalRecord.getAllergies());
                    }

                    return personWithMedicalRecordDTO;
                })
                .toList();

        return personWithMedicalRecordDTOS;

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
