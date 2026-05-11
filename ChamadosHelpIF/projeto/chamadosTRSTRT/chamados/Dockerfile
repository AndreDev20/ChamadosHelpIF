# Estágio de Build
# Usamos uma imagem que já tenha o JDK 25. 
# Nota: Como o Java 25 é muito recente, usamos a imagem base do OpenJDK
FROM openjdk:25-slim AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto para dentro do container
COPY . .

# Se o seu projeto usa Maven, descomente a linha abaixo:
# RUN ./mvnw clean package

# Se for Java puro (sem Maven/Gradle), vamos compilar manualmente:
# Isso compila todos os arquivos .java dentro de src para a pasta out
RUN mkdir -out && javac -d out $(find src -name "*.java")

# Estágio de Execução
FROM openjdk:25-slim

WORKDIR /app

# Copia o que foi compilado no estágio anterior
COPY --from=build /app/out .

# Comando para rodar a classe principal
# Substitua 'Main' pelo nome da sua classe principal (que tem o public static void main)
CMD ["java", "Main"]
