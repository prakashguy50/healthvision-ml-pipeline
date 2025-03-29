package com.healthvision.ml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/predict")
public class PredictionFunction {

    private static final DiabetesPredictor predictor = new DiabetesPredictor();

    @PostMapping
    public String predict(@RequestBody PredictionRequest request) {
        try {
            String prediction = predictor.predict(
                request.getPregnancies(), request.getGlucose(),
                request.getBloodPressure(), request.getSkinThickness(),
                request.getInsulin(), request.getBmi(),
                request.getDiabetesPedigreeFunction(), request.getAge()
            );
            return "{\"prediction\": \"" + prediction + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"Prediction failed: " + e.getMessage() + "\"}";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(PredictionFunction.class, args);
    }
}
