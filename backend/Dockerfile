# ESTÁGIO 1: Build (Compilação)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
# Copia apenas os arquivos do maven primeiro (para aproveitar o cache do Docker)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline

# Copia o código-fonte e compila
COPY src src
RUN ./mvnw clean package -DskipTests

# ESTÁGIO 2: Run (Execução)
# Usamos a JRE (muito mais leve que a JDK)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia APENAS o arquivo .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]