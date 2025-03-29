package com.healthvision.ml;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class DiabetesPredictor {
    private static final Logger logger = Logger.getLogger(DiabetesPredictor.class.getName());
    private final Classifier model;
    private final Instances datasetHeader;
    private final double predictionThreshold;

    public DiabetesPredictor() {
        try {
            // Initialize GCS client
            Storage storage = StorageOptions.getDefaultInstance().getService();
            
            // Load model from GCS
            Blob modelBlob = storage.get(
                System.getProperty("gcs.bucket", "healthvision-ml-20250328-23525"),
                System.getProperty("model.name", "iris-model.j48")
            );
            InputStream modelStream = new ByteArrayInputStream(modelBlob.getContent());
            this.model = (Classifier) SerializationHelper.read(modelStream);
            
            // Load dataset header for structure - FIXED: Using InputStreamReader
            Blob headerBlob = storage.get(
                System.getProperty("gcs.bucket", "healthvision-ml-20250328-23525"),
                System.getProperty("dataset.header", "dataset-header.arff")
            );
            InputStream headerStream = new ByteArrayInputStream(headerBlob.getContent());
            this.datasetHeader = new Instances(new InputStreamReader(headerStream), 0); // Added capacity parameter
            this.datasetHeader.setClassIndex(this.datasetHeader.numAttributes() - 1);
            
            this.predictionThreshold = Double.parseDouble(
                System.getProperty("prediction.threshold", "0.5")
            );
            
            logger.info("Diabetes predictor initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize DiabetesPredictor: " + e.getMessage());
            throw new RuntimeException("Failed to initialize DiabetesPredictor", e);
        }
    }

    public String predict(double pregnancies, double glucose, double bloodPressure,
                        double skinThickness, double insulin, double bmi,
                        double diabetesPedigreeFunction, double age) {
        try {
            // FIXED: Using DenseInstance instead of abstract Instance
            DenseInstance instance = new DenseInstance(datasetHeader.numAttributes());
            instance.setDataset(datasetHeader);
            
            // Set feature values (adjust indices based on your dataset structure)
            instance.setValue(0, pregnancies);
            instance.setValue(1, glucose);
            instance.setValue(2, bloodPressure);
            instance.setValue(3, skinThickness);
            instance.setValue(4, insulin);
            instance.setValue(5, bmi);
            instance.setValue(6, diabetesPedigreeFunction);
            instance.setValue(7, age);
            
            // Make prediction
            double[] distribution = model.distributionForInstance(instance);
            boolean isDiabetic = distribution[1] >= predictionThreshold;
            
            return isDiabetic ? "1" : "0"; // "1" for diabetic, "0" for non-diabetic
        } catch (Exception e) {
            logger.severe("Prediction failed: " + e.getMessage());
            throw new RuntimeException("Prediction failed", e);
        }
    }
}