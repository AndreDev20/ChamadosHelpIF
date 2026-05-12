# Estágio 1: Build com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o arquivo de configuração de dependências
COPY pom.xml .

# Baixa as dependências (Spring, Lombok, etc) - isso acelera builds futuros
RUN mvn dependency:go-offline

# Copia a pasta src
COPY src ./src

# Compila e gera o arquivo .jar (o pacote final da aplicação)
RUN mvn clean package -DskipTests

# Estágio 2: Execução
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o arquivo .jar que o Maven gerou na pasta target
# O '*' serve para pegar qualquer nome de arquivo que termine em .jar
COPY --from=build /app/target/*.jar app.jar

# Porta padrão
EXPOSE 8080

# Comando para rodar o Spring Boot
CMD ["java", "-jar", "app.jar"]
