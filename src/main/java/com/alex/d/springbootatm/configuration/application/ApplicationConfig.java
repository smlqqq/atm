package com.alex.d.springbootatm.configuration.application;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean(name = "org.OpenSolaris.configuration.SpringDocConfiguration.apiInfo")
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("ATM Web Application API")
                                .description("API for managing ATM operations")
                                .contact(
                                        new Contact()
                                                .name("API Support")
                                                .url("https://www.example.com/support")
                                                .email("support@example.com")
                                )
                                .license(
                                        new License()
                                                .name("Apache 2.0")
                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                                )
                                .version("0.0.1")
                )
                ;
    }
// TODO позже добавить весенний сесурити

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/api/v1/create",
//                                "/swagger-ui.html",       // Swagger UI
//                                "/v3/api-docs/**",        // OpenAPI JSON
//                                "/actuator/**"            // Actuator
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                );
//        return http.build();
//    }

}