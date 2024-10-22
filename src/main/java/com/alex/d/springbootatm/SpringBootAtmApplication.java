package com.alex.d.springbootatm;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootAtmApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        System.setProperty("LOCAL_DATABASE_URL", dotenv.get("LOCAL_DATABASE_URL"));
        System.setProperty("LOCAL_DATABASE_USERNAME", dotenv.get("LOCAL_DATABASE_USERNAME"));
        System.setProperty("LOCAL_DATABASE_PASSWORD", dotenv.get("LOCAL_DATABASE_PASSWORD"));

        SpringApplication.run(SpringBootAtmApplication.class, args);
    }

}
