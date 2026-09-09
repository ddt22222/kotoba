FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package -DskipTests
FROM eclipse-temurin:17-jre
RUN groupadd -r kotoba && useradd -r -g kotoba kotoba
WORKDIR /app
COPY --from=build /app/target/kotoba-api-1.0.0.jar app.jar
USER kotoba
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
