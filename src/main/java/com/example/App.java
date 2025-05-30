package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple Java Web Application for Azure DevOps CI/CD Demo
 * Web server version suitable for Azure Container Apps
 */
public class App {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean running = true;
    private HttpServer server;
    private final int port = 8080;
    
    public static void main(String[] args) {
        App app = new App();
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gracefully...");
            app.shutdown();
        }));
        
        System.out.println("=== Simple Java Web Application ===");
        System.out.println(app.getWelcomeMessage());
        System.out.println("Current time: " + app.getCurrentTime());
        
        try {
            // Start the web server
            app.startWebServer();
            System.out.println("Web server started on port " + app.port);
            System.out.println("Application started successfully!");
            
            // Start the periodic health check
            app.startPeriodicTask();
            
            // Keep the main thread alive
            app.keepAlive();
        } catch (IOException e) {
            System.err.println("Failed to start web server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Starts the HTTP web server
     */
    public void startWebServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Root endpoint
        server.createContext("/", new RootHandler());
        
        // Health check endpoint for Azure Container Apps
        server.createContext("/health", new HealthHandler());
        
        // API endpoints
        server.createContext("/api/welcome", new WelcomeHandler());
        server.createContext("/api/time", new TimeHandler());
        server.createContext("/api/add", new AddHandler());
        
        // Set executor for handling requests
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }
    
    /**
     * Starts a periodic task that runs every minute
     */
    public void startPeriodicTask() {
        scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                System.out.println("Health check: " + getCurrentTime() + " - Application is running");
            }
        }, 0, 60, TimeUnit.SECONDS);
    }
    
    /**
     * Keeps the application running
     */
    public void keepAlive() {
        try {
            while (running) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("Application interrupted");
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Shuts down the application gracefully
     */
    public void shutdown() {
        running = false;
        
        if (server != null) {
            server.stop(5);
            System.out.println("Web server stopped");
        }
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Returns a welcome message
     */
    public String getWelcomeMessage() {
        return "Hello from Azure DevOps CI/CD Pipeline!";
    }
    
    /**
     * Returns current formatted time
     */
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    /**
     * Adds two numbers
     */
    public int add(int a, int b) {
        return a + b;
    }
    
    /**
     * Checks if a string is empty or null
     */
    public boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    // HTTP Handlers
    
    class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<html><body>" +
                    "<h1>Simple Java Web Application</h1>" +
                    "<p>" + getWelcomeMessage() + "</p>" +
                    "<p>Current time: " + getCurrentTime() + "</p>" +
                    "<h2>Available Endpoints:</h2>" +
                    "<ul>" +
                    "<li><a href=\"/health\">/health</a> - Health check</li>" +
                    "<li><a href=\"/api/welcome\">/api/welcome</a> - Welcome message</li>" +
                    "<li><a href=\"/api/time\">/api/time</a> - Current time</li>" +
                    "<li>/api/add?a=5&b=3 - Add two numbers</li>" +
                    "</ul>" +
                    "</body></html>";
            
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"UP\",\"time\":\"" + getCurrentTime() + "\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    class WelcomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\":\"" + getWelcomeMessage() + "\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    class TimeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"currentTime\":\"" + getCurrentTime() + "\"}";
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String response;
            
            try {
                if (query != null) {
                    String[] params = query.split("&");
                    int a = 0, b = 0;
                    
                    for (String param : params) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2) {
                            if ("a".equals(keyValue[0])) {
                                a = Integer.parseInt(keyValue[1]);
                            } else if ("b".equals(keyValue[0])) {
                                b = Integer.parseInt(keyValue[1]);
                            }
                        }
                    }
                    
                    int result = add(a, b);
                    response = "{\"a\":" + a + ",\"b\":" + b + ",\"result\":" + result + "}";
                } else {
                    response = "{\"error\":\"Missing parameters. Use: /api/add?a=5&b=3\"}";
                }
            } catch (NumberFormatException e) {
                response = "{\"error\":\"Invalid number format\"}";
            }
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}