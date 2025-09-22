FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy parent pom and module POMs
COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY product-service/pom.xml product-service/
COPY order-service/pom.xml order-service/
COPY nova-lib/pom.xml nova-lib/

# Copy sources
COPY user-service/src user-service/src
COPY product-service/src product-service/src
COPY order-service/src order-service/src
COPY nova-lib/src nova-lib/src

# Build only this module (and required deps). Produces target/user-service.jar (fat jar)
RUN mvn -pl user-service -am clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/user-service/target/user-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
