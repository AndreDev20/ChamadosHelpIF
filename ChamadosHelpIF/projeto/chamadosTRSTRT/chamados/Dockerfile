# Estágio 1: Build com Maven e Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e gera o JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução com JRE 17
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia o JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8080 (Padrão Spring Boot)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
