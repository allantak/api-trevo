FROM maven:3.8.6-openjdk-11-slim
WORKDIR /app
COPY . .
ENV DATASOURCE_URL=jdbc:postgresql://10.0.0.43:5432/trevo \
    DATASOURCE_USERNAME=postgres \
    DATASOURCE_PASSWORD=12345 \
    JWT_SECRET=12345
RUN mvn package
EXPOSE 8081
CMD ["java", "-jar", "/app/target/trevo-0.0.1-SNAPSHOT.jar"]