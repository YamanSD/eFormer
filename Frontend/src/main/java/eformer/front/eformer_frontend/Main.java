package eformer.front.eformer_frontend;

import eformer.front.eformer_frontend.connector.RequestsGateway;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Main extends Application {
    public static Image getImage(String name) {
        return new Image(Objects.requireNonNull(Main.class.getResource("/images/" + name)).toString());
    }

    @Override
    public void start(Stage stage) {
        /* Patches the illegalAccess issues of jfoenix */
        org.burningwave.core.assembler.StaticComponentContainer.Modules.exportAllToAll();

        Optional<Pair<String, String>> result = Optional.empty();
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();

        while (result.isEmpty()) {
            dialog = new Dialog<>();
            dialog.setTitle("Login");
            dialog.setHeaderText("Welcome!");

            // Set the icon (must be included in the project).
            var image = new ImageView(Objects.requireNonNull(Main.class.getResource("/images/login.png")).toString());

            image.setFitWidth(100);
            image.setFitHeight(100);
            dialog.setGraphic(image);

            // Set the button types.
            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            // Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");
            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);

            // Enable/Disable login button depending on whether a username was entered.
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            // Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) -> {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(username::requestFocus);

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(username.getText(), password.getText());
                }
                return null;
            });

            dialog.setOnHidden(response -> {

            });

            result = dialog.showAndWait();

            if (result.isPresent()) {
                RequestsGateway.authenticate(result.get().getKey(), result.get().getValue());

                if (RequestsGateway.getToken() == null) {
                    result = Optional.empty();
                }
            } else {
                System.exit(0);
            }
        }

        dialog.close();
        stage.getIcons().clear();
        stage.getIcons().add(getImage("cart.png"));
        stage.setFullScreenExitHint("");
        stage.setTitle("eFormer");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
                if (ke.getCode() == KeyCode.ESCAPE) {
                    System.exit(0);
                }
            });

            stage.setScene(scene);
            stage.setResizable(true);
            stage.setFullScreen(true);
            stage.setMaximized(true);
        }
        catch (IOException ex) {
            RequestsGateway.displayException(ex);
        }

        stage.setWidth(450);
        stage.setHeight(280);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}