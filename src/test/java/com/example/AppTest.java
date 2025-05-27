package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for App class
 */
public class AppTest {
    
    private App app;
    
    @BeforeEach
    void setUp() {
        app = new App();
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
}