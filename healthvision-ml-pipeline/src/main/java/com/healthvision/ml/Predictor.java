package com.healthvision.ml;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Predictor {
    private final Classifier model;
    private static final String MODEL_PATH = "gs://healthvision-ml-20250328-23525/models/iris-model.j48";
    
    public Predictor() throws Exception {
        this.model = loadModel();
    }
    
    private Classifier loadModel() throws Exception {
        // Create temp file
        File tempFile = File.createTempFile("model-", ".j48");
        tempFile.deleteOnExit();
        
        // Download from GCS
        new GCSClient().downloadModel(MODEL_PATH, tempFile.getAbsolutePath());
        
        return (Classifier) weka.core.SerializationHelper.read(tempFile.getAbsolutePath());
    }
    
    public String predict(double sepalLength, double sepalWidth, 
                        double petalLength, double petalWidth) throws Exception {
        Instances dummy = new DataSource(getClass().getResourceAsStream("/datasets/iris.csv")).getDataSet();
        dummy.setClassIndex(dummy.numAttributes() - 1);
        
        dummy.instance(0).setValue(0, sepalLength);
        dummy.instance(0).setValue(1, sepalWidth);
        dummy.instance(0).setValue(2, petalLength);
        dummy.instance(0).setValue(3, petalWidth);
        
        double pred = model.classifyInstance(dummy.instance(0));
        return dummy.classAttribute().value((int)pred);
    }
}