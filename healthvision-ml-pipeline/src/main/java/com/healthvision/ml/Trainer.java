package com.healthvision.ml;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.classifiers.trees.J48;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Trainer {
    private static final String DEFAULT_BUCKET = "gs://healthvision-ml-20250328-23525";
    
    public static void main(String[] args) {
        try {
            System.out.println("Starting model training...");
            
            // 1. Validate and parse arguments
            if (args.length < 1) {
                System.err.println("Usage: Trainer <input-data-path> [model-name]");
                System.err.println("For classpath resources, use prefix 'classpath:'");
                System.exit(1);
            }
            
            String inputPath = args[0];
            String modelName = (args.length > 1) ? args[1] : "iris-model";
            
            // 2. Load data (handling both filesystem and classpath resources)
            System.out.println("Loading data from: " + inputPath);
            Instances data = loadData(inputPath);
            data.setClassIndex(data.numAttributes() - 1);
            System.out.printf("Loaded dataset with %d instances, %d attributes%n", 
                           data.numInstances(), data.numAttributes());

            // 3. Train model
            J48 classifier = new J48();
            classifier.buildClassifier(data);
            System.out.println("Model training completed");

            // 4. Save and upload
            String localModelPath = modelName + ".j48";
            weka.core.SerializationHelper.write(localModelPath, classifier);
            System.out.println("Model saved locally at: " + localModelPath);

            // 5. Upload to GCS
            String gcsPath = String.format("%s/models/%s.j48", DEFAULT_BUCKET, modelName);
            new GCSClient().uploadModel(localModelPath, gcsPath);
            
            // 6. Clean up
            Files.deleteIfExists(new File(localModelPath).toPath());
            
        } catch (Exception e) {
            System.err.println("Error in model training: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Loads dataset from either filesystem or classpath
     */
    private static Instances loadData(String resourcePath) throws Exception {
        File dataFile;
        
        if (resourcePath.startsWith("classpath:")) {
            // Load from classpath resources
            String internalPath = resourcePath.replace("classpath:", "").trim();
            InputStream inputStream = Trainer.class.getClassLoader().getResourceAsStream(internalPath);
            
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + internalPath);
            }
            
            // Create temp file
            dataFile = File.createTempFile("temp-", "-iris.csv");
            Files.copy(inputStream, dataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            dataFile.deleteOnExit();
        } else {
            // Load from filesystem
            dataFile = new File(resourcePath);
            if (!dataFile.exists()) {
                throw new IllegalArgumentException("File not found: " + resourcePath);
            }
        }
        
        CSVLoader loader = new CSVLoader();
        loader.setSource(dataFile);
        return loader.getDataSet();
    }
}