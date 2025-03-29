package com.healthvision.ml;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.classifiers.trees.J48;
import java.io.File;

public class Trainer {
    private static final String DEFAULT_BUCKET = "gs://healthvision-ml-20250328-23525";
    
    public static void main(String[] args) {
        try {
            System.out.println("Starting model training...");
            
            // Validate arguments
            if (args.length < 1) {
                System.err.println("Usage: Trainer <input-csv-path> [output-model-name]");
                System.exit(1);
            }
            
            String inputPath = args[0];
            String modelName = (args.length > 1) ? args[1] : "iris-model";
            
            // 1. Load data
            System.out.println("Loading data from: " + inputPath);
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(inputPath));
            Instances data = loader.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            System.out.printf("Loaded dataset with %d instances, %d attributes%n", 
                           data.numInstances(), data.numAttributes());

            // 2. Train model
            J48 classifier = new J48();
            classifier.buildClassifier(data);
            System.out.println("Model training completed");

            // 3. Save and upload
            String localModelPath = modelName + ".j48";
            weka.core.SerializationHelper.write(localModelPath, classifier);
            System.out.println("Model saved locally at: " + localModelPath);

            // 4. Upload to GCS
            String gcsPath = String.format("%s/models/%s.j48", DEFAULT_BUCKET, modelName);
            new GCSClient().uploadModel(localModelPath, gcsPath);
            
        } catch (Exception e) {
            System.err.println("Error in model training: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}