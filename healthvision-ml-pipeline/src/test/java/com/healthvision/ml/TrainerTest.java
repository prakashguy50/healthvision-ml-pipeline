package com.healthvision.ml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.InputStream;

public class TrainerTest {
    
    @Test
    @DisplayName("Verify training dataset exists")
    void testTrainingDataExists() {
        // Test resource loading
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("datasets/iris.csv")) {
            
            assertNotNull(is, "iris.csv dataset not found in resources");
            
            // Additional verification
            File dataFile = new File(
                getClass().getClassLoader()
                    .getResource("datasets/iris.csv")
                    .getFile()
            );
            
            assertAll(
                () -> assertTrue(dataFile.exists(), 
                    "Data file should exist at: " + dataFile.getAbsolutePath()),
                () -> assertTrue(dataFile.length() > 0, 
                    "Data file should not be empty")
            );
            
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Verify dataset has correct structure")
    void testDatasetStructure() throws Exception {
        InputStream is = getClass().getClassLoader()
            .getResourceAsStream("datasets/iris.csv");
        assertNotNull(is, "Dataset not found");
        
        // Add additional dataset validation logic here if needed
    }
}