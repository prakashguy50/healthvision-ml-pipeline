package com.healthvision.ml;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

public class PredictionFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(PredictionFunction.class.getName());
    private static final Gson gson = new Gson();
    private static Classifier model;
    private static Instances datasetHeader;
    
    static {
        try {
            // Initialize GCS client
            Storage storage = StorageOptions.getDefaultInstance().getService();
            
            // Load model from GCS
            Blob modelBlob = storage.get("healthvision-ml-20250328-23525", "iris-model.j48");
            InputStream modelStream = new ByteArrayInputStream(modelBlob.getContent());
            model = (Classifier) SerializationHelper.read(modelStream);
            
            // Load dataset header for structure
            Blob headerBlob = storage.get("healthvision-ml-20250328-23525", "dataset-header.arff");
            InputStream headerStream = new ByteArrayInputStream(headerBlob.getContent());
            datasetHeader = new Instances(headerStream);
            datasetHeader.setClassIndex(datasetHeader.numAttributes() - 1);
            
            logger.info("Model and dataset header loaded successfully");
        } catch (Exception e) {
            logger.severe("Initialization failed: " + e.getMessage());
            throw new RuntimeException("Initialization failed", e);
        }
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) 
            throws IOException {
        response.setContentType("application/json");
        BufferedWriter writer = response.getWriter();
        
        try {
            JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
            JsonObject responseJson = new JsonObject();
            
            if (requestJson.has("sepalLength") && requestJson.has("sepalWidth") &&
                requestJson.has("petalLength") && requestJson.has("petalWidth")) {
                
                // Create instance for prediction
                Instance instance = new Instance(datasetHeader.numAttributes());
                instance.setDataset(datasetHeader);
                instance.setValue(0, requestJson.get("sepalLength").getAsDouble());
                instance.setValue(1, requestJson.get("sepalWidth").getAsDouble());
                instance.setValue(2, requestJson.get("petalLength").getAsDouble());
                instance.setValue(3, requestJson.get("petalWidth").getAsDouble());
                
                // Make prediction
                double prediction = model.classifyInstance(instance);
                String predictedClass = datasetHeader.classAttribute().value((int)prediction);
                
                responseJson.addProperty("prediction", predictedClass);
                writer.write(gson.toJson(responseJson));
            } else {
                response.setStatusCode(400);
                responseJson.addProperty("error", "Missing required parameters");
                writer.write(gson.toJson(responseJson));
            }
        } catch (JsonSyntaxException e) {
            response.setStatusCode(400);
            writer.write("{\"error\":\"Invalid JSON format\"}");
        } catch (Exception e) {
            logger.severe("Prediction error: " + e.getMessage());
            response.setStatusCode(500);
            writer.write("{\"error\":\"Internal server error\"}");
        }
    }
}