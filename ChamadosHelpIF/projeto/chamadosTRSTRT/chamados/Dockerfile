# Estágio 1: Compilação
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia todo o conteúdo da pasta do projeto
COPY . .

# Cria a pasta de saída e compila todos os arquivos .java
# O find busca todos os arquivos dentro de src/ e o javac compila para a pasta out/
RUN mkdir -p out
RUN find src -name "*.java" > sources.txt
RUN javac -d out @sources.txt

# Estágio 2: Execução (Imagem leve)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia apenas os arquivos compilados (os .class) do estágio anterior
COPY --from=build /app/out .

# Porta padrão do Render
EXPOSE 8080

# --- CONFIGURAÇÃO MANUAL IMPORTANTE ---
# No IntelliJ, você tem uma classe com o método: public static void main(String[] args)
# Se o arquivo for "Principal.java" e não tiver "package" no topo, use: CMD ["java", "Principal"]
# Se ele estiver dentro de um pacote, use: CMD ["java", "nome.do.pacote.Principal"]
CMD ["java", "Main"]
