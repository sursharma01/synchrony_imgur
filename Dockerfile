# Use lightweight OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-alpine as build

# Set a working directory
WORKDIR /app

# Copy the Maven build artifact (your JAR file)
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Set JVM options for better performance (optional)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Start the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]