package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple Java Application for Azure DevOps CI/CD Demo
 * Long-running version suitable for containers
 */
public class App {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean running = true;
    
    public static void main(String[] args) {
        App app = new App();
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gracefully...");
            app.shutdown();
        }));
        
        System.out.println("=== Simple Java Application ===");
        System.out.println(app.getWelcomeMessage());
        System.out.println("Current time: " + app.getCurrentTime());
        System.out.println("Application started successfully!");
        
        // Start the long-running process
        app.startPeriodicTask();
        
        // Keep the main thread alive
        app.keepAlive();
    }
    
    /**
     * Starts a periodic task that runs every minute
     */
    public void startPeriodicTask() {
        scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                System.out.println("Health check: " + getCurrentTime() + " - Application is running");
            }
        }, 0, 60, TimeUnit.SECONDS); // Run every 60 seconds
    }
    
    /**
     * Keeps the application running
     */
    public void keepAlive() {
        try {
            while (running) {
                Thread.sleep(1000); // Sleep for 1 second
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
     * @return welcome message string
     */
    public String getWelcomeMessage() {
        return "Hello from Azure DevOps CI/CD Pipeline!";
    }
    
    /**
     * Returns current formatted time
     * @return formatted current time
     */
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    /**
     * Adds two numbers
     * @param a first number
     * @param b second number
     * @return sum of a and b
     */
    public int add(int a, int b) {
        return a + b;
    }
    
    /**
     * Checks if a string is empty or null
     * @param str string to check
     * @return true if string is empty or null
     */
    public boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}