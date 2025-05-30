package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Unit tests for App class (Web Application version)
 */
public class AppTest {
    
    private App app;
    private HttpClient httpClient;
    private final String baseUrl = "http://localhost:8080";
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        app = new App();
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        // Start the web server for integration tests
        app.startWebServer();
        
        // Give the server a moment to start
        Thread.sleep(500);
    }
    
    @AfterEach
    void tearDown() {
        // Ensure proper cleanup after each test
        app.shutdown();
    }
    
    @Test
    void testGetWelcomeMessage() {
        String message = app.getWelcomeMessage();
        assertNotNull(message);
        assertTrue(message.contains("Azure DevOps"));
        assertEquals("Hello from Azure DevOps CI/CD Pipeline!", message);
    }
    
    @Test
    void testGetCurrentTime() {
        String time = app.getCurrentTime();
        assertNotNull(time);
        assertFalse(time.isEmpty());
        // Check format yyyy-MM-dd HH:mm:ss
        assertTrue(time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
    
    @Test
    void testAdd() {
        assertEquals(5, app.add(2, 3));
        assertEquals(0, app.add(0, 0));
        assertEquals(-1, app.add(2, -3));
        assertEquals(100, app.add(50, 50));
    }
    
    @Test
    void testIsEmpty() {
        assertTrue(app.isEmpty(null));
        assertTrue(app.isEmpty(""));
        assertTrue(app.isEmpty("   "));
        assertFalse(app.isEmpty("hello"));
        assertFalse(app.isEmpty("  hello  "));
    }
    
    @Test
    void testStartPeriodicTask() {
        // Test that the periodic task can be started without throwing exceptions
        assertDoesNotThrow(() -> {
            app.startPeriodicTask();
            Thread.sleep(100); // Small delay to ensure task starts
        });
    }
    
    @Test
    void testShutdown() {
        // Test that shutdown executes without throwing exceptions
        assertDoesNotThrow(() -> {
            app.startPeriodicTask();
            app.shutdown();
        });
    }
    
    // Web Application Integration Tests
    
    @Test
    void testRootEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/"))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Simple Java Web Application"));
        assertTrue(response.body().contains("Azure DevOps"));
    }
    
    @Test
    void testHealthEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/health"))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""));
        assertTrue(response.body().contains("\"time\""));
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(""));
    }
    
    @Test
    void testWelcomeApiEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/welcome"))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"message\""));
        assertTrue(response.body().contains("Azure DevOps"));
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(""));
    }
    
    @Test
    void testTimeApiEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/time"))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"currentTime\""));
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(""));
    }
    
    @Test
    void testAddApiEndpoint() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/add?a=5&b=3"))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        String body = response.body();
        assertTrue(body.contains("\"a\":5"));
        assertTrue(body.contains("\"b\":3"));
        assertTrue(body.contains("\"result\":8"));
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElse(""));
    }
    
    @Test
    void testAddApiEndpointWithoutParameters() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/add"))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"error\""));
        assertTrue(response.body().contains("Missing parameters"));
    }
    
    @Test
    void testAddApiEndpointWithInvalidParameters() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/add?a=invalid&b=3"))
                .timeout(Duration.ofSeconds(10))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"error\""));
        assertTrue(response.body().contains("Invalid number format"));
    }
}