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
            this.predictor = new Predictor();
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
            JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
            
            // Validate request
            if (!requestJson.has("sepalLength") || !requestJson.has("sepalWidth") || 
                !requestJson.has("petalLength") || !requestJson.has("petalWidth")) {
                throw new IllegalArgumentException("Missing required fields");
            }
            
            String prediction = predictor.predict(
                requestJson.get("sepalLength").getAsDouble(),
                requestJson.get("sepalWidth").getAsDouble(),
                requestJson.get("petalLength").getAsDouble(),
                requestJson.get("petalWidth").getAsDouble()
            );
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("prediction", prediction);
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
}