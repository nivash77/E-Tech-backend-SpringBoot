# ------------ Stage 1: Build the JAR using Maven + Java 21 ------------
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (use cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the rest of the project
COPY . .

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# ------------ Stage 2: Run the JAR using JDK 21 runtime ------------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy built JAR file from previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 9091

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]