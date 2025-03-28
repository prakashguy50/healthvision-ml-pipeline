package com.healthvision.ml;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.classifiers.trees.J48;
import java.io.File;

public class Trainer {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting model training...");
        
        // 1. Load data
        CSVLoader loader = new CSVLoader();
		loader.setSource(new File("/app/iris.csv"));
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        System.out.println("Loaded dataset with " + data.numInstances() + " instances");

        // 2. Train model
        J48 classifier = new J48();
        classifier.buildClassifier(data);
        System.out.println("Model training completed");

        // 3. Save and upload
        String modelPath = "iris-model.j48";
        weka.core.SerializationHelper.write(modelPath, classifier);
        new GCSClient().uploadModel(modelPath);
        System.out.println("Model uploaded to GCS");
    }
}