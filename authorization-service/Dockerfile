# ---- Construcción ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY authorization-service/pom.xml ./authorization-service/pom.xml
COPY authorization-service/mvnw ./authorization-service/mvnw
COPY authorization-service/.mvn ./authorization-service/.mvn

# Descargar dependencias Maven
RUN ./authorization-service/mvnw -f authorization-service/pom.xml dependency:go-offline -B

# Permite copiar el código fuente
COPY authorization-service/src ./authorization-service/src

# Se compila el código utilizando el pom.xml del authorization service
RUN ./authorization-service/mvnw -f authorization-service/pom.xml clean package -DskipTests -B

# ---- EJECUCIÓN ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/authorization-service/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]