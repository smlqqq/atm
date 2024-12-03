package com.alex.d.springbootatm.configuration.env;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DotEnvConfig {

    public Dotenv createConfig() {

        Dotenv dotenv = Dotenv.load();

        System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
        System.setProperty("DB_SCHEMA", Objects.requireNonNull(dotenv.get("DB_SCHEMA")));
        System.setProperty("DB_USERNAME", Objects.requireNonNull(dotenv.get("DB_USERNAME")));
        System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
        System.setProperty("PRELIQUIBASE_SCHEMA_NAME", Objects.requireNonNull(dotenv.get("PRELIQUIBASE_SCHEMA_NAME")));

        return dotenv;
    }
}
