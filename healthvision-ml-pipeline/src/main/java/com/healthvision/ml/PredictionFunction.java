@Override
public void service(HttpRequest request, HttpResponse response) throws IOException {
    response.setContentType("application/json");
    BufferedWriter writer = response.getWriter();
    
    try {
        JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
        
        // Handle both formats
        if (requestJson.has("input")) {
            // Simple test format
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("status", "alive");
            responseJson.addProperty("message", "Service is running");
            responseJson.addProperty("received_input", requestJson.get("input").getAsString());
            writer.write(gson.toJson(responseJson));
        } 
        else if (requestJson.has("sepalLength") && requestJson.has("sepalWidth") && 
                 requestJson.has("petalLength") && requestJson.has("petalWidth")) {
            // Actual prediction format
            String prediction = predictor.predict(
                requestJson.get("sepalLength").getAsDouble(),
                requestJson.get("sepalWidth").getAsDouble(),
                requestJson.get("petalLength").getAsDouble(),
                requestJson.get("petalWidth").getAsDouble()
            );
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("prediction", prediction);
            writer.write(gson.toJson(responseJson));
        }
        else {
            throw new IllegalArgumentException(
                "Requires either {input} or {sepalLength,sepalWidth,petalLength,petalWidth}"
            );
        }
        
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