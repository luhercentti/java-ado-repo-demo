# Use OpenJDK 11 as base image
FROM openjdk:11-jre-slim

# Set working directory inside container
WORKDIR /app

# Accept JAR file name as build argument
ARG JAR_FILE=*.jar

# Copy the JAR file from target directory to container
COPY target/${JAR_FILE} app.jar

# Expose port 8080 for the web server
EXPOSE 8080

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Health check using wget (available in openjdk:11-jre-slim)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Set JVM options as environment variable
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the Java application with JVM tuning
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]