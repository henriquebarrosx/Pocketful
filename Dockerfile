# Usar a imagem oficial do Gradle para construir o .jar com Java 17
FROM gradle:7.5.0-jdk17 AS build

# Definir o diretório de trabalho dentro do contêiner
WORKDIR /home/gradle/project

# Copiar todos os arquivos do projeto para o diretório de trabalho
COPY . .

# Executar o build do Gradle para criar o .jar
RUN ./gradlew build

# Segunda etapa: usar uma imagem mais leve para rodar o .jar com Java 17
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar o .jar construído para o novo contêiner
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Definir o comando de entrada para rodar o .jar
ENTRYPOINT ["java", "-jar", "app.jar"]
