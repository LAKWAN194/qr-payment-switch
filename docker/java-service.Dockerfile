# syntax=docker/dockerfile:1
#
# Builds one Spring Boot module. Run from the repository root:
#   docker build -f docker/java-service.Dockerfile --build-arg MODULE=switch-service .
# compose.yaml does this for both services.

FROM eclipse-temurin:21-jdk-alpine AS build
ARG MODULE
WORKDIR /src
COPY .mvn .mvn
COPY mvnw pom.xml ./
# Maven needs every module's pom to read the reactor, even when building only one.
COPY switch-service/pom.xml switch-service/
COPY mock-issuer/pom.xml mock-issuer/
COPY ${MODULE}/src ${MODULE}/src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -pl ${MODULE} -am package -DskipTests \
    && cp ${MODULE}/target/${MODULE}-*.jar /src/app.jar

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /src/app.jar app.jar
USER app
ENTRYPOINT ["java", "-jar", "app.jar"]
