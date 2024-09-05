# Use the official Maven image to build the app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image to run the app
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the packaged jar file
COPY --from=build /app/target/spring-boot-atm-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

#docker build -t spring-boot-atm .
#docker run -p 8080:8080 spring-boot-atm