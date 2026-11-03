# ---- Construcción ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY authentication-service/pom.xml ./authentication-service/pom.xml
COPY authentication-service/mvnw ./authentication-service/mvnw
COPY authentication-service/.mvn ./authentication-service/.mvn

# Descargar dependencias Maven
RUN ./authentication-service/mvnw -f authentication-service/pom.xml dependency:go-offline -B

# Permite copiar el código fuente
COPY authentication-service/src ./authentication-service/src

# Se compila el código utilizando el pom.xml del authentication service
RUN ./authentication-service/mvnw -f authentication-service/pom.xml clean package -DskipTests -B

# ---- EJECUCIÓN ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/authentication-service/target/*.jar app.jar
EXPOSE 8082

ENTRYPOINT [ "java", "-jar", "app.jar" ]