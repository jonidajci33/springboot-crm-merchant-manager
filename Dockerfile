FROM maven:3.8.5-openjdk-17 as build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/aegis_broker-0.0.1-SNAPSHOT.jar merchant_manager.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "merchant_manager.jar"]
