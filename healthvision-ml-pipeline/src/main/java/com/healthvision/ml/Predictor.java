package com.healthvision.ml;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import java.io.File;
import java.io.InputStream;

public class Predictor {
    private Classifier model;
    
    public Predictor() throws Exception {
        String modelPath = "gs://healthvision-ml-20250328-23525/models/iris-model.j48";
        this.model = loadModel(modelPath);
    }
    
    private Classifier loadModel(String modelPath) throws Exception {
        File tempFile = File.createTempFile("model-", ".j48");
        tempFile.deleteOnExit();
        new GCSClient().downloadModel(modelPath, tempFile.getAbsolutePath());
        return (Classifier) weka.core.SerializationHelper.read(tempFile.getAbsolutePath());
    }
    
    public String predict(double sepalLength, double sepalWidth,
                        double petalLength, double petalWidth) throws Exception {
        InputStream is = getClass().getResourceAsStream("/datasets/iris.csv");
        Instances dummy = new DataSource(is).getDataSet();
        dummy.setClassIndex(dummy.numAttributes() - 1);
        
        dummy.instance(0).setValue(0, sepalLength);
        dummy.instance(0).setValue(1, sepalWidth);
        dummy.instance(0).setValue(2, petalLength);
        dummy.instance(0).setValue(3, petalWidth);
        
        double pred = model.classifyInstance(dummy.instance(0));
        return dummy.classAttribute().value((int)pred);
    }
}