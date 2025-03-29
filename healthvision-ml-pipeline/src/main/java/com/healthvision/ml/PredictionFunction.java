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

public class PredictionFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(PredictionFunction.class.getName());
    private final Predictor predictor;
    private final Gson gson = new Gson();
    
    public PredictionFunction() {
        try {
            // Initialize predictor with model from GCS
            String modelPath = "gs://healthvision-ml-20250328-23525/models/iris-model.j48";
            this.predictor = new Predictor(modelPath);
            logger.info("Predictor initialized successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize predictor", e);
        }
    }
    
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.setContentType("application/json");
        BufferedWriter writer = response.getWriter();
        
        try {
            // Parse and validate request
            JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
            validateRequest(requestJson);
            
            // Extract features
            double sepalLength = requestJson.get("sepalLength").getAsDouble();
            double sepalWidth = requestJson.get("sepalWidth").getAsDouble();
            double petalLength = requestJson.get("petalLength").getAsDouble();
            double petalWidth = requestJson.get("petalWidth").getAsDouble();
            
            // Make prediction
            String prediction = predictor.predict(sepalLength, sepalWidth, petalLength, petalWidth);
            
            // Prepare response
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("prediction", prediction);
            responseJson.addProperty("model", "iris-classifier");
            responseJson.addProperty("version", "1.0");
            
            writer.write(gson.toJson(responseJson));
            
        } catch (JsonSyntaxException e) {
            response.setStatusCode(400);
            writer.write("{\"error\":\"Invalid JSON format\"}");
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            writer.write("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.severe("Prediction error: " + e.getMessage());
            response.setStatusCode(500);
            writer.write("{\"error\":\"Internal server error\"}");
        }
    }
    
    private void validateRequest(JsonObject requestJson) {
        if (!requestJson.has("sepalLength") || !requestJson.has("sepalWidth") || 
            !requestJson.has("petalLength") || !requestJson.has("petalWidth")) {
            throw new IllegalArgumentException("Missing required fields in request");
        }
        
        try {
            requestJson.get("sepalLength").getAsDouble();
            requestJson.get("sepalWidth").getAsDouble();
            requestJson.get("petalLength").getAsDouble();
            requestJson.get("petalWidth").getAsDouble();
        } catch (ClassCastException | IllegalStateException e) {
            throw new IllegalArgumentException("All features must be numeric values");
        }
    }
}