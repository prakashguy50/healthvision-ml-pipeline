package com.healthvision.ml;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import weka.core.DenseInstance;
import weka.core.Instances;

public class PredictionFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(PredictionFunction.class.getName());
    private static final Gson gson = new Gson();
    private final DiabetesPredictor predictor = new DiabetesPredictor();

    @Override
    public void service(HttpRequest request, HttpResponse response) 
            throws IOException {
        response.setContentType("application/json");
        BufferedWriter writer = response.getWriter();
        
        try {
            JsonObject requestJson = gson.fromJson(new InputStreamReader(request.getInputStream()), JsonObject.class);
            JsonObject responseJson = new JsonObject();
            
            if (requestJson.has("pregnancies") && requestJson.has("glucose") &&
                requestJson.has("bloodPressure") && requestJson.has("skinThickness") &&
                requestJson.has("insulin") && requestJson.has("bmi") &&
                requestJson.has("diabetesPedigreeFunction") && requestJson.has("age")) {
                
                String prediction = predictor.predict(
                    requestJson.get("pregnancies").getAsDouble(),
                    requestJson.get("glucose").getAsDouble(),
                    requestJson.get("bloodPressure").getAsDouble(),
                    requestJson.get("skinThickness").getAsDouble(),
                    requestJson.get("insulin").getAsDouble(),
                    requestJson.get("bmi").getAsDouble(),
                    requestJson.get("diabetesPedigreeFunction").getAsDouble(),
                    requestJson.get("age").getAsDouble()
                );
                
                responseJson.addProperty("prediction", prediction);
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