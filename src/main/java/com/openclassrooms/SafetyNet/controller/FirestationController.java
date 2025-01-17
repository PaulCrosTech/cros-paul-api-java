package com.openclassrooms.SafetyNet.controller;

import com.openclassrooms.SafetyNet.exceptions.CustomApiError;
import com.openclassrooms.SafetyNet.model.Firestation;
import com.openclassrooms.SafetyNet.service.FirestationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * FirestationController class
 */
@Tag(name = "Firestation", description = "API")
@Log4j2
@RestController
public class FirestationController {

    private final FirestationService firestationService;

    /**
     * Constructor
     *
     * @param firestationService PersonService
     */
    public FirestationController(FirestationService firestationService) {
        log.info("<constructor> FirestationController");
        this.firestationService = firestationService;
    }

    /**
     * Get all fire stations
     *
     * @return List of Fire objects
     */
    @Operation(summary = "Get all fire stations", description = "Returns all fire stations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/firestations", headers = "X-API-VERSION=1")
    public List<Firestation> getFirestations() {
        log.info("<controller> **New** Request GET on /firestations");
        return firestationService.getFirestations();
    }

    /**
     * Delete a fire station by address
     *
     * @param address String address of the fire station (case-sensitive)
     */
    @Operation(summary = "Delete a fire station by his address", description = "Delete a fire station by his address (case-sensitive)")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "address", description = "The address of fire station", required = true, example = "\"834 Binoc Ave\""),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found - The fire station was not found")
    })
    @DeleteMapping(path = "/firestation", params = "address", headers = "X-API-VERSION=1")
    public void deletePerson(@RequestParam String address) throws Exception {
        log.info("<controller> **New** Request DELETE on /firestation?address={}", address);
        firestationService.deleteFirestationByAddress(address);
    }


    /**
     * Create a fire station
     *
     * @param firestation Firestation object to create
     * @return Firestation object created
     */
    @Operation(summary = "Create a fire station", description = "Add a new fire station")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created", content = @Content
            ),
            @ApiResponse(responseCode = "400", description = "Bad request - The request is invalid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiError.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - The fire station already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomApiError.class))
            )
    })
    @PostMapping(path = "/firestation", headers = "X-API-VERSION=1")
    public ResponseEntity<Object> createFirestation(@Valid @RequestBody Firestation firestation) {
        log.info("<controller> **New** Request POST on /firestation body {}", firestation);

        firestationService.saveFirestation(firestation);

        // Create the location of the person object saved
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{address}")
                .buildAndExpand(firestation.getAddress())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Update a fire station
     *
     * @param firestation Firestation object to update
     * @return Firestation object updated
     */
    @Operation(summary = "Update a fire station", description = "Update a fire station by his address (case-sensitive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not found - The fire station was not found", content = @Content)
    })
    @PutMapping(path = "/firestation", headers = "X-API-VERSION=1")
    public Firestation updateFirestation(@Valid @RequestBody Firestation firestation) {
        log.info("<controller> **New** Request PUT on /firestation body {}", firestation);

        return firestationService.updateFirestation(firestation);
    }
}
