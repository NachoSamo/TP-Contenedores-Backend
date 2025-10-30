# Multi-stage Dockerfile: build with Maven (Java 21) then run on JDK 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace
# copy only what is needed for the multi-module build
COPY pom.xml ./
COPY common-data/pom.xml common-data/pom.xml
COPY ms-flota/pom.xml ms-flota/pom.xml
COPY ms-rutas/pom.xml ms-rutas/pom.xml
COPY ms-solicitudes/pom.xml ms-solicitudes/pom.xml
COPY gateway/pom.xml gateway/pom.xml
# copy full source tree
COPY . .
# build only the module and required modules
RUN mvn -pl ms-flota -am -DskipTests package

FROM eclipse-temurin:21-jdk
WORKDIR /app
# copy the built jar from the build stage
COPY --from=build /workspace/ms-flota/target/ms-flota-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=default
ENTRYPOINT ["java","-jar","/app/app.jar"]
