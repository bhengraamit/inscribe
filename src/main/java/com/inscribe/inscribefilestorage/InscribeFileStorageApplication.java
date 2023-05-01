package com.inscribe.inscribefilestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan(basePackages = "com.inscribe.inscribefilestorage")
@SpringBootApplication
public class InscribeFileStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(InscribeFileStorageApplication.class, args);
    }

}
