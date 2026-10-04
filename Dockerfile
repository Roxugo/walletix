# Etapa 1: compila el proyecto con Maven y Java 17
FROM maven:3.9-eclipse-temurin-17 AS construccion
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Etapa 2: imagen liviana solo con Java para ejecutar el .jar
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=construccion /app/target/walletix-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Render indica el puerto en la variable PORT; en local se usa 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
