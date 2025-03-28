package com.healthvision.ml;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import java.io.File;

public class TrainerTest {
    @Test
    public void testDataLoading() throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("datasets/iris.csv"));  // Relative path from project root
        Instances data = loader.getDataSet();
        assertEquals(150, data.numInstances());
        assertEquals(5, data.numAttributes());
    }
}