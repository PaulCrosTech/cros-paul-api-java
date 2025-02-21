package com.openclassrooms.SafetyNet;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SafetyNetApplication Class
 */
@Log4j2
@SpringBootApplication
public class SafetyNetApplication {

    /**
     * Main method
     *
     * @param args String[]
     */
    public static void main(String[] args) {

        SpringApplication.run(SafetyNetApplication.class, args);
        log.info("==> Application SafetyNet started <==");
    }
}
