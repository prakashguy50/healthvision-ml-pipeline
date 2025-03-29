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
            logger.info("Initializing DiabetesPredictor...");
            
            // Initialize GCS client
            Storage storage = StorageOptions.getDefaultInstance().getService();
            
            // Load model from GCS
            String bucketName = System.getProperty("gcs.bucket", "healthvision-ml-20250328-23525");
            String modelName = System.getProperty("model.name", "iris-model.j48");
            
            logger.info("Loading model from gs://" + bucketName + "/" + modelName);
            Blob modelBlob = storage.get(bucketName, modelName);
            if (modelBlob == null) {
                throw new RuntimeException("Model file not found in GCS");
            }
            
            try (InputStream modelStream = new ByteArrayInputStream(modelBlob.getContent())) {
                this.model = (Classifier) SerializationHelper.read(modelStream);
            }
            
            // Load dataset header
            String headerName = System.getProperty("dataset.header", "dataset-header.arff");
            logger.info("Loading header from gs://" + bucketName + "/" + headerName);
            Blob headerBlob = storage.get(bucketName, headerName);
            if (headerBlob == null) {
                throw new RuntimeException("Dataset header not found in GCS");
            }
            
            try (InputStream headerStream = new ByteArrayInputStream(headerBlob.getContent());
                 InputStreamReader reader = new InputStreamReader(headerStream)) {
                this.datasetHeader = new Instances(reader);
                this.datasetHeader.setClassIndex(this.datasetHeader.numAttributes() - 1);
            }
            
            this.predictionThreshold = Double.parseDouble(
                System.getProperty("prediction.threshold", "0.5")
            );
            
            logger.info("DiabetesPredictor initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize DiabetesPredictor: " + e.getMessage());
            throw new RuntimeException("Failed to initialize DiabetesPredictor", e);
        }
    }

    public String predict(double pregnancies, double glucose, double bloodPressure,
                        double skinThickness, double insulin, double bmi,
                        double diabetesPedigreeFunction, double age) {
        try {
            DenseInstance instance = new DenseInstance(datasetHeader.numAttributes());
            instance.setDataset(datasetHeader);
            
            instance.setValue(0, pregnancies);
            instance.setValue(1, glucose);
            instance.setValue(2, bloodPressure);
            instance.setValue(3, skinThickness);
            instance.setValue(4, insulin);
            instance.setValue(5, bmi);
            instance.setValue(6, diabetesPedigreeFunction);
            instance.setValue(7, age);
            
            double[] distribution = model.distributionForInstance(instance);
            boolean isDiabetic = distribution[1] >= predictionThreshold;
            
            return isDiabetic ? "1" : "0";
        } catch (Exception e) {
            logger.severe("Prediction failed: " + e.getMessage());
            throw new RuntimeException("Prediction failed", e);
        }
    }
}