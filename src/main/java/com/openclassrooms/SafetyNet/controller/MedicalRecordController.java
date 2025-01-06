package com.openclassrooms.SafetyNet.controller;

import com.openclassrooms.SafetyNet.exceptions.CustomApiError;
import com.openclassrooms.SafetyNet.model.MedicalRecord;
import com.openclassrooms.SafetyNet.model.MedicalRecordUpdateDTO;
import com.openclassrooms.SafetyNet.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Medical Record", description = "Medical Records API")
@Log4j2
@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * Constructor
     *
     * @param medicalRecordService MedicalRecordService
     */
    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        log.info("<constructor> MedicalRecordController");
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * Get all medical records
     *
     * @return List of MedicalRecords objects
     */
    @Operation(summary = "Get all medical records", description = "Returns all medical records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/medicalRecord", headers = "X-API-VERSION=1")
    public List<MedicalRecord> getMedicalRecords() {
        log.info("<controller> **New** Request GET on /medicalRecord (Version 1)");
        return medicalRecordService.getMedicalRecords();
    }

    /**
     * Get a medical record by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     * @return MedicalRecord object
     */
    @Operation(summary = "Get a medical record by first name and last name", description = "Returns a medical record by his first name and last name.<br>Names are case-sensitive")
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - The medical record was not found", content = @Content)
    })
    @GetMapping(path = "/medicalRecord/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public MedicalRecord getMedicalRecordByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("<controller> **New** Request GET on /medicalRecord/{}/{}", firstName, lastName);
        return medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Delete a medical record by first name and last name
     *
     * @param firstName String case-sensitive
     * @param lastName  String case-sensitive
     */
    @Operation(summary = "Delete a medical record by first name and last name", description = "Delete a medical record by his first name and last name.<br>Names are case-sensitive")
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found - The medical record was not found")
    })
    @DeleteMapping(path = "/medicalRecord/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public void deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) throws Exception {
        log.info("<controller> **New** Request DELETE on /medicalRecord/{}/{}", firstName, lastName);
        medicalRecordService.deleteMedicalRecordByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Create - Add a new medical record
     *
     * @param medicalRecord A MedicalRecord object
     * @return The location of MedicalRecord object saved
     */
    @Operation(summary = "Create a medical record", description = "Add a new medical record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successfully created",
                    content = @Content
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request - The request is invalid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict - The medical record already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiError.class))
            )
    })
    @PostMapping(path = "/medicalRecord", headers = "X-API-VERSION=1")
    public ResponseEntity<Object> createMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        log.info("<controller> **New** Request POST on /medicalRecord {}", medicalRecord);

        medicalRecordService.saveMedicalRecord(medicalRecord);

        // Create the location of the person object saved
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}/{lastName}")
                .buildAndExpand(medicalRecord.getFirstName(), medicalRecord.getLastName())
                .toUri();
        return ResponseEntity.created(location).build();
    }


    /**
     * Update a medical record
     *
     * @param firstName     firstName of the medical record to be updated
     * @param lastName      lastName of the medical record to be updated
     * @param medicalRecord MedicalRecordUpdateDTO object to update
     * @return MedicalRecord object updated
     */
    @Operation(summary = "Update a medical record", description = "Update a medical record by his first name and last name.<br>Names are case-sensitive")
    @Parameters({
            @Parameter(name = "firstName", description = "The first name of the person", required = true, example = "John"),
            @Parameter(name = "lastName", description = "The last name of the person", required = true, example = "Boyd")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - The medical record was not found", content = @Content)
    })
    @PutMapping(path = "/medicalRecord/{firstName}/{lastName}", headers = "X-API-VERSION=1")
    public MedicalRecord updateMedicalRecord(@PathVariable String firstName, @PathVariable String lastName, @Valid @RequestBody MedicalRecordUpdateDTO medicalRecord) {
        log.info("<controller> **New** Request PUT on /medicalRecord {}", medicalRecord);

        return medicalRecordService.updateMedicalRecord(firstName, lastName, medicalRecord);
    }
}
