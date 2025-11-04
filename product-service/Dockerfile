FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy all pom files first to leverage Docker caching
COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY product-service/pom.xml product-service/
COPY order-service/pom.xml order-service/
COPY nova-lib/pom.xml nova-lib/
COPY auth-service/pom.xml auth-service/
COPY gateway-service/pom.xml gateway-service/

# Download dependencies (cached unless pom.xml changes)
RUN mvn -B dependency:go-offline

# Copy source code
COPY user-service/src user-service/src
COPY product-service/src product-service/src
COPY order-service/src order-service/src
COPY nova-lib/src nova-lib/src
COPY auth-service/src auth-service/src
COPY gateway-service/src gateway-service/src

# Build only product-service module
RUN mvn -pl product-service -am clean package -DskipTests

# --- RUNTIME STAGE ---
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/product-service/target/product-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
