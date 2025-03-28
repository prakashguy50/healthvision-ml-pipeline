package com.healthvision.ml;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class TrainerTest {
    @Test
    public void testDataLoading() throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("datasets/iris.csv"));
        Instances data = loader.getDataSet();
        assertEquals(150, data.numInstances());
        assertEquals(5, data.numAttributes());
    }
}