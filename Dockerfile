FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*
RUN useradd -r -u 1001 appuser
COPY --from=build /workspace/target/franchise-management-api-0.0.1-SNAPSHOT.jar /app/app.jar
RUN chown -R appuser:appuser /app
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 CMD ["sh", "-c", "wget -qO- http://localhost:8080/actuator/health || exit 1"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
