package com.healthvision.ml;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

public class TrainerTest {
    
    @Test
    public void testDataLoading() throws Exception {
        // Load test dataset from resources
        String testDataPath = getClass()
            .getClassLoader()
            .getResource("datasets/iris.csv")
            .getFile();
        
        File dataFile = new File(testDataPath);
        
        // Verify file exists and is readable
        assertTrue(dataFile.exists(), "Test data file should exist");
        assertTrue(dataFile.length() > 0, "Test data file should not be empty");
    }
}