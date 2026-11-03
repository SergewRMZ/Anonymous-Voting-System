# ---- Construcción ----
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY bulletin-board/pom.xml ./bulletin-board/pom.xml
COPY bulletin-board/mvnw ./bulletin-board/mvnw
COPY bulletin-board/.mvn ./bulletin-board/.mvn

# Descargar dependencias Maven
RUN ./bulletin-board/mvnw -f bulletin-board/pom.xml dependency:go-offline -B

# Permite copiar el código fuente
COPY bulletin-board/src ./bulletin-board/src

# Se compila el código utilizando el pom.xml del bulletin board
RUN ./bulletin-board/mvnw -f bulletin-board/pom.xml clean package -DskipTests -B

# ---- EJECUCIÓN ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/bulletin-board/target/*.jar app.jar
EXPOSE 8085

ENTRYPOINT [ "java", "-jar", "app.jar" ]