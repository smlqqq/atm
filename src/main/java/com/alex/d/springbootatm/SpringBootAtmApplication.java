package com.alex.d.springbootatm;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootAtmApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_SCHEMA", dotenv.get("DB_SCHEMA"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(SpringBootAtmApplication.class, args);
    }

}
