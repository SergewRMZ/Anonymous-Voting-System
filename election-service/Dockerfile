# ---- Construcción ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY election-service/pom.xml ./election-service/pom.xml
COPY election-service/mvnw ./election-service/mvnw
COPY election-service/.mvn ./election-service/.mvn

# Descargar dependencias Maven
RUN ./election-service/mvnw -f election-service/pom.xml dependency:go-offline -B

# Permite copiar el código fuente
COPY election-service/src ./election-service/src

# Se compila el código utilizando el pom.xml del election service
RUN ./election-service/mvnw -f election-service/pom.xml clean package -DskipTests -B

# ---- EJECUCIÓN ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/election-service/target/*.jar app.jar
EXPOSE 8083

ENTRYPOINT [ "java", "-jar", "app.jar" ]