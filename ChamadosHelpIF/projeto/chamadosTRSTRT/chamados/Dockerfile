# Estágio de Compilação
FROM openjdk:21-slim AS build
WORKDIR /app

# Copia tudo da pasta atual para o container
COPY . .

# Compila todos os arquivos .java dentro da pasta src
# Se você tiver bibliotecas .jar na pasta lib, precisará incluí-las aqui
RUN mkdir -p out && find src -name "*.java" > sources.txt
RUN javac -d out @sources.txt

# Estágio de Execução
FROM openjdk:21-slim
WORKDIR /app

# Copia o que foi compilado
COPY --from=build /app/out .

# O Render precisa que a aplicação escute em uma porta (geralmente 8080 ou 10000)
EXPOSE 8080

# IMPORTANTE: Troque 'Main' pelo nome da sua classe principal
# Se o seu programa for uma interface gráfica (Swing/AWT), ele NÃO vai funcionar no Render,
# pois o Render é apenas para aplicações Web ou APIs.
CMD ["java", "Main"]
