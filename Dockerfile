# Imagen base con Java 21
FROM eclipse-temurin:21-jdk


# Directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el jar generado en target/
COPY target/*jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar el JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]