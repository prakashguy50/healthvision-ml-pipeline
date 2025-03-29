package com.healthvision.ml;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import java.io.File;

public class Predictor {
    private static final String DEFAULT_BUCKET = "gs://healthvision-ml-20250328-23525";
    private final Classifier model;
    
    public Predictor(String modelPath) throws Exception {
        // Handle GCS paths
        if (modelPath.startsWith("gs://")) {
            String localPath = "downloaded-model.j48";
            new GCSClient().downloadModel(modelPath, localPath);
            this.model = (Classifier) weka.core.SerializationHelper.read(localPath);
            new File(localPath).delete(); // Clean up
        } else {
            this.model = (Classifier) weka.core.SerializationHelper.read(modelPath);
        }
    }
    
    public String predict(double[] features) throws Exception {
        // Load dummy dataset structure
        Instances dummy = new DataSource("datasets/iris.csv").getDataSet();
        dummy.setClassIndex(dummy.numAttributes() - 1);
        
        // Set feature values
        for (int i = 0; i < features.length; i++) {
            dummy.instance(0).setValue(i, features[i]);
        }
        
        // Make prediction
        double pred = model.classifyInstance(dummy.instance(0));
        return dummy.classAttribute().value((int)pred);
    }
}