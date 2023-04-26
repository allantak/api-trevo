FROM maven:3.8.6-openjdk-11-slim
WORKDIR /app
COPY . .
RUN mvn package
EXPOSE 8081
CMD ["java", "-jar", "/app/target/trevo-0.0.1-SNAPSHOT.jar"]