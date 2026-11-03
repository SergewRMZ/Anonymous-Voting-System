# ---- Construcción ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY tally-service/pom.xml ./tally-service/pom.xml
COPY tally-service/mvnw ./tally-service/mvnw
COPY tally-service/.mvn ./tally-service/.mvn

# Descargar dependencias Maven
RUN ./tally-service/mvnw -f tally-service/pom.xml dependency:go-offline -B

# Permite copiar el código fuente
COPY tally-service/src ./tally-service/src

# Se compila el código utilizando el pom.xml del tally service
RUN ./tally-service/mvnw -f tally-service/pom.xml clean package -DskipTests -B

# ---- EJECUCIÓN ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/tally-service/target/*.jar app.jar
EXPOSE 8084

ENTRYPOINT [ "java", "-jar", "app.jar" ]