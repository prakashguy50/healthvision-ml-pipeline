package com.healthvision.ml;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;

public class TrainerTest {
    
    @Test
    public void testDataLoading() throws Exception {
        // Use test resource path
        String testDataPath = getClass()
            .getClassLoader()
            .getResource("datasets/iris.csv")
            .getFile();
        
        File dataFile = new File(testDataPath);
        assertTrue("Test data file should exist", dataFile.exists());
        assertTrue("Test data file should not be empty", dataFile.length() > 0);
    }
}