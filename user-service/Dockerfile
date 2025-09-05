# Stage 1: Build the service
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy module poms
COPY user-service/pom.xml user-service/
COPY product-service/pom.xml product-service/
COPY order-service/pom.xml order-service/
COPY nova-lib/pom.xml nova-lib/

# Copy source code
COPY user-service/src user-service/src
COPY product-service/src product-service/src
COPY order-service/src order-service/src
COPY nova-lib/src nova-lib/src

# Build only user-service + its dependencies
RUN mvn -pl user-service -am clean package -DskipTests

# Stage 2: Run the jar
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
