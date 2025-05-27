package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple Java Application for Azure DevOps CI/CD Demo
 */
public class App {
    
    public static void main(String[] args) {
        App app = new App();
        System.out.println("=== Simple Java Application ===");
        System.out.println(app.getWelcomeMessage());
        System.out.println("Current time: " + app.getCurrentTime());
        System.out.println("Application started successfully!");
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