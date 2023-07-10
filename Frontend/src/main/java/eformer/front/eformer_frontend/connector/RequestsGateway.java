package eformer.front.eformer_frontend.connector;

import eformer.front.eformer_frontend.controller.DashboardController;
import eformer.front.eformer_frontend.model.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class RequestsGateway {
    private final static String urlBase = "http://localhost:8080/api/v1/";

    private static String token = null;

    private static User current = null;

    public static void displayWarning(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    public static void displayException(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("An error occurred");
        alert.setContentText(ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    private static Object processResponse(HttpURLConnection connection) throws Exception {
        var response = new StringBuilder();

        try (var reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(),
                        StandardCharsets.UTF_8))
        ) {
            String responseLine;

            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        try {
            return new JSONObject(response.toString());
        } catch (JSONException ignored) {
            try {
                return new JSONArray(response.toString());
            } catch (JSONException ignored2) {
                return response.toString();
            }
        }
    }

    protected static Object executePostRequest(
            String target,
            Object body,
            String token
    ) {
        try {
            URL url = new URL(urlBase + target);

            var connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (token != null) {
                connection.setRequestProperty("Authorization", getToken());
            }

            if (body != null) {
                try (var out = connection.getOutputStream()) {
                    byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                    out.write(input, 0, input.length);
                }
            }

            return processResponse(connection);
        } catch (Exception e) {
            displayException(e);
            return e;
        }
    }

    protected static Object get(
            String target,
            JSONObject params
    ) {
        try {
            if (params != null) {
                StringBuilder targetBuilder = new StringBuilder(target).append('?');
                for (var param: params.keySet()) {
                    targetBuilder.append(param).append('=').append(params.get(param)).append('&');
                }

                target = targetBuilder.toString();
            }

            URL url = new URL(urlBase + target);

            var connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            return processResponse(connection);
        } catch (Exception e) {
            displayException(e);
            return e;
        }
    }

    protected static Object post(String target, Object body) {
        return executePostRequest(target, body, token);
    }

    public static User getCurrentUser() {
        return current;
    }

    public static void authenticate(String username, String password) {
        try {
            var body = new JSONObject();

            body.put("username", username);
            body.put("password", password);

            var response = (JSONObject) post("auth/authenticate", body);
            setToken(response.getString("token"));
            current = UsersConnector.getByUsername(username);
        } catch (Exception e) {
            displayException(e);
        }
    }

    public static void register(JSONObject body) {
        try {
            var response = (JSONObject) post("auth/register", body);
            setToken(response.getString("token"));
            current = UsersConnector.getByUsername(body.getString("username"));
        } catch (Exception e) {
            displayException(e);
        }
    }

    public static void logout() {
        token = null;
    }

    public static <T> void mapToObject(JSONObject props, T result) {
        for (var prop: props.keySet()) {
            try {
                var value = props.get(prop);

                /* Use reflection to call setters */
                result.getClass().getDeclaredMethod(
                        "set"
                                + prop.substring(0, 1).toUpperCase()
                                + prop.substring(1),
                        value.getClass()
                ).invoke(result, value);
            } catch (Exception ignored) {
            }
        }
    }

    public static void setToken(String newToken) {
        token = newToken;
    }

    public static String getToken() {
        return token != null ? "Bearer " + token : token;
    }
}
