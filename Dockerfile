# ---------- 1) BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

# ---------- 2) RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
RUN useradd -r -u 1001 appuser
WORKDIR /app

COPY --from=build /src/target/*-SNAPSHOT.jar /app/app.jar
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE="default"

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
