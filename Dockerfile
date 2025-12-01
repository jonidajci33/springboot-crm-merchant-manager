FROM maven:3.9-eclipse-temurin-21 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/merchant_manager-0.0.1-SNAPSHOT.war merchant_manager.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "merchant_manager.war"]
