package com.openclassrooms.SafetyNet.mapper;

import com.openclassrooms.SafetyNet.dto.*;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.Person;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EmergencyMapper Class
 */
@Log4j2
@Component
public class EmergencyMapper {


    /**
     * Convert a Person to a PersonAtSameAddressDTO
     *
     * @param personWithBirthdate map of person with birthdate
     * @return the converted PersonAtSameAddressDTO
     */
    public PersonCoveredByStationDTO toPersonCoveredByStationDTO(Map<Person, String> personWithBirthdate) {

        PersonCoveredByStationDTO personCoveredByStationDTO = new PersonCoveredByStationDTO();

        int nbAdults = 0;
        int nbChildren = 0;
        List<PersonBasicDetailsDTO> personBasicDetailsDTO = new ArrayList<>();

        for (Map.Entry<Person, String> entry : personWithBirthdate.entrySet()) {
            Person person = entry.getKey();
            String birthdate = entry.getValue();

            personBasicDetailsDTO.add(new PersonBasicDetailsDTO(
                    person.getFirstName(),
                    person.getLastName(),
                    person.getAddress(),
                    person.getPhone()));

            int age = calculateAge(birthdate);
            if (age >= 18) {
                nbAdults++;
            } else {
                nbChildren++;
            }
        }

        personCoveredByStationDTO.setNbAdults(nbAdults);
        personCoveredByStationDTO.setNbChildren(nbChildren);
        personCoveredByStationDTO.setPersons(personBasicDetailsDTO);
        return personCoveredByStationDTO;

    }

    /**
     * Convert Persons (with birthdate) to a HouseChildrenDTO
     *
     * @param personWithBirthdate map of person with birthdate
     * @return the converted HouseChildrenDTO
     */
    public List<HouseChildrenDTO> toHouseChildrenDTO(Map<Person, String> personWithBirthdate) {

        List<HouseChildrenDTO> houseChildrenDTOList = new ArrayList<>();

        personWithBirthdate.forEach((person, birthdate) -> {
            int age = calculateAge(birthdate);
            if (age <= 18) {

                List<HouseMemberDTO> houseMemberDTOList = personWithBirthdate.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals(person))
                        .map(entry -> new HouseMemberDTO(entry.getKey().getFirstName(), entry.getKey().getLastName(), calculateAge(entry.getValue())))
                        .toList();

                houseChildrenDTOList.add(
                        new HouseChildrenDTO(
                                person.getFirstName(),
                                person.getLastName(),
                                age,
                                houseMemberDTOList
                        )
                );
            }
        });

        return houseChildrenDTOList;
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
                        personWithMedicalRecordDTO.getAge(),
                        personWithMedicalRecordDTO.getPhone(),
                        personWithMedicalRecordDTO.getMedications(),
                        personWithMedicalRecordDTO.getAllergies()

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
        personWithMedicalAndPhoneDTO.setAge((person.getAge() != null) ? person.getAge() : null);
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
        personWithMedicalAndEmailDTO.setAge((person.getAge() != null) ? person.getAge() : null);
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

        return persons.stream()
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

    }

    /**
     * Calcule l'âge d'une personne
     *
     * @param birthdate date de naissance
     * @return âge
     */
    private int calculateAge(String birthdate) {
        // Définir le format de la date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Convertir la chaîne de caractères en LocalDate
        LocalDate birthDateLocal = LocalDate.parse(birthdate, formatter);

        // Obtenir la date actuelle
        LocalDate today = LocalDate.now();

        // Calculer la différence en années entre la date de naissance et la date actuelle
        return Period.between(birthDateLocal, today).getYears();
    }
}
