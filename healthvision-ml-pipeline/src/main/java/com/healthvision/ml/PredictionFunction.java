import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class PredictionFunction extends HttpServlet {
    private static final Logger logger = Logger.getLogger(PredictionFunction.class.getName()); // Fixed typo here
    private final Gson gson = new Gson();
    private final IrisPredictor predictor = new IrisPredictor();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (BufferedReader reader = request.getReader();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {

            JsonObject requestJson = gson.fromJson(reader, JsonObject.class);

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
                        "Requires either {input} or {sepalLength, sepalWidth, petalLength, petalWidth}"
                );
            }

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid JSON format\"}");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.severe("Prediction error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }
}