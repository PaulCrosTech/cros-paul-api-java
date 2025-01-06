package com.openclassrooms.SafetyNet.controller;


import com.openclassrooms.SafetyNet.model.AddressOccupants;
import com.openclassrooms.SafetyNet.model.PersonCoveredByStation;
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
            @Parameter(in = ParameterIn.QUERY, name = "address", description = "The address", required = true, example = "112 Steppes Pl"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping(path = "/childAlert", params = "address", headers = "X-API-VERSION=1")
    public AddressOccupants getOccupantsByAddress(@RequestParam String address) {
        log.info("<controller> **New** Request GET on /childAlert?address={}", address);
        return emergencyService.getOccupantsByAddress(address);
    }

}
