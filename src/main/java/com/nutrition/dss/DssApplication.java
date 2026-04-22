package com.nutrition.dss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 *  MAIN ENTRY POINT
 *  Run this class to start the entire application.
 *  In IntelliJ/Eclipse: Right-click → Run As → Java Application
 *  In Terminal: mvn spring-boot:run
 * ============================================================
 */
@SpringBootApplication
public class DssApplication {
    public static void main(String[] args) {
        SpringApplication.run(DssApplication.class, args);
    }
}
