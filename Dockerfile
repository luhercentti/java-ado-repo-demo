# Use OpenJDK 11 as base image
FROM openjdk:11-jre-slim

# Set working directory inside container
WORKDIR /app

# Copy the JAR file from target directory to container
COPY target/simple-java-app-1.0.0-shaded.jar app.jar

# Expose port 8080 (if your app has a web server, otherwise this is optional)
EXPOSE 8080

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD java -version || exit 1

# Run the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add JVM tuning for containers
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]