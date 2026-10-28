# ---- Construcción ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY api-gateway/mvnw ./api-gateway/mvnw
COPY api-gateway/.mvn ./api-gateway/.mvn

# Descargar dependencias Maven
RUN ./api-gateway/mvnw -f api-gateway/pom.xml dependency:go-offline -B

# Permite copiar el código fuente
COPY api-gateway/src ./api-gateway/src

# Se compila el código utilizando el pom.xml del api gateway
RUN ./api-gateway/mvnw -f api-gateway/pom.xml clean package -DskipTests -B

# ---- EJECUCIÓN ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 9000

ENTRYPOINT [ "java", "-jar", "app.jar" ]