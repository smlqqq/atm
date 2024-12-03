package com.alex.d.springbootatm;

import com.alex.d.springbootatm.configuration.env.DotEnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootAtmApplication {

    public static void main(String[] args) {

        DotEnvConfig config = new DotEnvConfig();
        config.createConfig();

        SpringApplication.run(SpringBootAtmApplication.class, args);
    }

}
