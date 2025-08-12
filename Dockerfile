# Usa una imagen oficial de OpenJDK como base
FROM eclipse-temurin:17-jdk-jammy

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR de la aplicación
COPY target/*.jar app.jar

# Expone el puerto en el que corre la aplicación
EXPOSE 6060

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]