# ATM 

A RESTful API created using Spring Boot. This project provides basic functionalities of an ATM system. It allows users to perform operations such as create/delete card number and pin code, withdrawing money, checking balance, and transferring funds.
    
    Technologies Used

    - Java 17
    - Spring Boot 3.2.2
    - Spring Data JPA
    - Spring Web
    - Spring Security
    - PostgreSQL
    - Liquibase Migration
    - Pre Liquibase
    - Springdoc OpenAPI (Swagger UI)
    - Lombok
    - SLF4J for logging
    - Kafka
    - Docker
    - Dot Env

## Dependencies

- **Spring Boot Starter Data JPA**: Provides support for Spring Data JPA with Hibernate for working with databases.
- **Spring Boot Starter Web**: Starter for building web applications using Spring MVC.
- **PostgreSQL Driver**: JDBC driver for connecting to the PostgreSQL database.
- **Spring Boot Starter Test**: Starter for testing Spring Boot applications with libraries including JUnit, Hamcrest, and Mockito.
- **Spring Boot Starter Thymeleaf**: Integrates Thymeleaf with Spring Boot for server-side rendering of HTML.
- **Lombok**: Java library to reduce boilerplate code in models and entities.
- **Pre Liquibase**: Used for creating schema before migration start.
- **Liquibase**: Database migration tool for managing database changes.
- **SLF4J API**: Simple Logging Facade for Java, provides a logging API that can be used with various logging frameworks.
- **JCL Over SLF4J**: Redirects Jakarta Commons Logging (JCL) to SLF4J for unified logging.
- **Springdoc OpenAPI Starter WebMVC UI**: Integrates OpenAPI (formerly Swagger UI) with Spring Boot for API documentation.
- **Kafka**: Supports event streaming and messaging capabilities within the application.
- **Docker**: Used for containerizing the application, simplifying deployment in various environments.
- **Dot Env**: Used for hide credentials.

## Setup and Installation

   -  **Clone the repo from GitHub**
       ```
        git clone https://github.com/smlqqq/atm.git
        cd spring-boot-atm
       ```

   -  **Set up a PostgreSQL database and configure the connection properties in application.properties.**
     
   -  **(Optional) Update database configurations in application.properties**
     If your database is hosted at some cloud platform or if you have modified the SQL script file with some different username and password, update the src/main/resources/application.properties file accordingly:
      ``` 
       spring.datasource.url=${DB_URL}${DB_SCHEMA}
       spring.datasource.username=${DB_USERNAME}
       spring.datasource.password=${DB_PASSWORD}
      ```

   - **Run the spring boot application**
      ```
      ./mvnw spring-boot:run
      ```  
     
   - **Build & Run Docker image**
      ```
      docker build -t atm .
      docker run -p 8080:8080 atm
      ```


this runs at port 8080 and hence all enpoints can be accessed starting from http://localhost:8080
