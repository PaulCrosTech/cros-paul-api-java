package com.openclassrooms.SafetyNet.controller;


import com.openclassrooms.SafetyNet.model.*;
import com.openclassrooms.SafetyNet.service.EmergencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;


@Tag(name = "Emergency", description = "Emergency API")
@Log4j2
@RestController
public class EmergencyController {

    private final EmergencyService emergencyService;

    /**
     * Constructor
     *
     * @param emergencyService EmergencyService object
     */
    @Autowired
    public EmergencyController(EmergencyService emergencyService) {
        log.info("<constructor> FirestationController");
        this.emergencyService = emergencyService;
    }


    /**
     * Get person covered by a fire stations
     *
     * @param stationNumber The station number
     * @return PersonCoveredByStation object
     */
    @Operation(summary = "Get person covered by a fire stations", description = "Returns all person covered by a fire stations, with adults and children count")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "stationNumber", description = "The station number", required = true, example = "3"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/firestation", params = "stationNumber", headers = "X-API-VERSION=1")
    public PersonCoveredByStation getPersonCoveredByStation(@RequestParam int stationNumber) {
        log.info("<controller> **New** Request GET on /firestation?stationNumber={}", stationNumber);
        return emergencyService.getPersonCoveredByStationNumber(stationNumber);
    }


    /**
     * Get child, with parent information, by address
     *
     * @param address The address
     * @return AddressOccupants object
     */
    @Operation(summary = "Get child, with parent information, by address", description = "Returns all child by address, with parent informations")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "address", description = "The address", required = true, example = "\"112 Steppes Pl\""),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/childAlert", params = "address", headers = "X-API-VERSION=1")
    public PersonAtSameAddress getPersonAtSameAddress(@RequestParam String address) {
        log.info("<controller> **New** Request GET on /childAlert?address={}", address);
        return emergencyService.getPersonAtSameAddress(address);
    }


    /**
     * Get phone number covered by a fire stations
     *
     * @param firestation The station number
     * @return HashSet of phone numbers
     */
    @Operation(summary = "Get person's phone number covered by a fire stations", description = "Returns all person's phone number covered by a fire stations")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "firestation", description = "The station number", required = true, example = "3"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/phoneAlert", params = "firestation", headers = "X-API-VERSION=1")
    public HashSet<String> getPhoneNumbersCoveredByFireStation(@RequestParam String firestation) {
        log.info("<controller> **New** Request GET on /phoneAlert?firestation={}", firestation);
        return emergencyService.getPhoneNumbersCoveredByFireStation(firestation);
    }


    /**
     * Get persons at same address with fire station and medical details
     *
     * @param address The address
     * @return List of PersonAtSameAddressWithFirestation objects
     */
    @Operation(summary = "Get persons at same address with fire station details", description = "Returns all persons living at same address with fire station details")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "address", description = "The address", required = true, example = "\"112 Steppes Pl\""),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/fire", params = "address", headers = "X-API-VERSION=1")
    public PersonAtSameAddressWithMedicalDetailsAndFirestation getPersonsAtSameAddressWithMedicalDetailsAndFirestation(@RequestParam String address) {
        log.info("<controller> **New** Request GET on /fire?address={}", address);
        return emergencyService.getPersonsAtSameAddressWithMedicalDetailsAndFirestation(address);
    }


    /**
     * Get List of person, with medical details, grouped by address
     *
     * @param stations List of stations number
     * @return List of PersonWithMedicalDetailsGroupedByAddress objects
     */
    @Operation(summary = "Get persons at same address with fire station details", description = "Returns all persons living at same address with fire station details")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "stations", description = "List of stations number", required = true, example = "[3,1]"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/flood/stations", params = "stations", headers = "X-API-VERSION=1")
    public PersonWithMedicalDetailsGroupedByAddress getPersonsGroupedByAddress(@RequestParam List<Integer> stations) {
        log.info("<controller> **New** Request GET on /flood/stations");
        return emergencyService.getPersonWithMedicalDetailsGroupedByAddress(stations);
    }


    /**
     * Get persons, with medical details and email, by last name
     *
     * @param lastName The last name
     * @return List of PersonMedicalDetailsWithEmail objects
     */
    @Operation(summary = "Get persons, with medical details and email, by last name", description = "Returned all persons, with medical details and email, by last name")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "lastName", description = "Last name", required = true, example = "Boyd"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/personInfo", params = "lastName", headers = "X-API-VERSION=1")
    public List<PersonMedicalDetailsWithEmail> getPersonMedicalDetailsWithEmail(@RequestParam String lastName) {
        log.info("<controller> **New** Request GET on /personInfo?lastName={}", lastName);
        return emergencyService.getPersonMedicalDetailsWithEmail(lastName);
    }


    /**
     * Get persons email by city name
     *
     * @param city The city
     * @return HashSet of email
     */
    @Operation(summary = "", description = "")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "city", description = "City", required = true, example = "Culver"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/communityEmail", params = "city", headers = "X-API-VERSION=1")
    public HashSet<String> getPersonEmailByCity(String city) {
        log.info("<controller> **New** Request GET on /communityEmail?city={}", city);
        return emergencyService.getPersonEmailByCity(city);
    }
}