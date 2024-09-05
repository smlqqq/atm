# ATM 

A RESTful API created using Spring Boot. This project provides basic functionalities of an ATM system. It allows users to perform operations such as create/delete card number and pin code, withdrawing money, checking balance, and transferring funds.
    
    Technologies Used

    - Java 17
    - Spring Boot 3.2.2
    - Spring Data JPA
    - Spring Web
    - Spring Security
    - PostgreSQL
    - Flyway Migration
    - Springdoc OpenAPI (Swagger UI)
    - Lombok
    - SLF4J for logging
    - Kafka
    - Docker

## Dependencies

   - Spring Boot Starter Data JPA: Provides support for Spring Data JPA with Hibernate.
   - Spring Boot Starter Web: Starter for building web applications using Spring MVC.
   - PostgreSQL Driver: PostgreSQL JDBC driver for connecting to the PostgreSQL database.
   - Spring Boot Starter Test: Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest, and Mockito.
   - Spring Boot Starter Thymeleaf: Integrates Thymeleaf with Spring Boot for server-side rendering of HTML.
   - Lombok: Java library to reduce boilerplate code for models and entities.
   - Flyway Core: Database migration tool for managing database changes.
   - Flyway Database PostgreSQL: Flyway extension for PostgreSQL database migrations.
   - SLF4J API: Simple Logging Facade for Java.
   - Log4j Over SLF4J: Redirects SLF4J logging to Log4j.
   - JCL Over SLF4J: Redirects Jakarta Commons Logging (JCL) to SLF4J.
   - Springdoc OpenAPI Starter WebMVC UI: Integrates OpenAPI (formerly Swagger UI) with Spring Boot for API documentation.

## Setup and Installation

   -  **Clone the repo from GitHub**
       ```
        git clone https://github.com/smlqqq/spring-boot-atm.git
        cd spring-boot-atm
       ```

   -  **Set up a PostgreSQL database and configure the connection properties in application.properties.**
     
   -  **(Optional) Update database configurations in application.properties**
     If your database is hosted at some cloud platform or if you have modified the SQL script file with some different username and password, update the src/main/resources/application.properties file accordingly:
      ``` 
       spring.datasource.url=jdbc:postgresql://localhost:5432/bank_db
       spring.datasource.username=postgres
       spring.datasource.password=postgres
      ```

   - **Run the spring boot application**
      ```
      ./mvnw spring-boot:run
      ```

this runs at port 8080 and hence all enpoints can be accessed starting from http://localhost:8080
