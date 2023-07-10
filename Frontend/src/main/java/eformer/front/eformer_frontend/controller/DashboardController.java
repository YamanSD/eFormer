/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eformer.front.eformer_frontend.controller;


import java.io.IOException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import eformer.front.eformer_frontend.connector.OrdersConnector;
import eformer.front.eformer_frontend.connector.RequestsGateway;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 */
public class DashboardController implements Initializable {
    @FXML
    private StackPane holderPane;

    AnchorPane inventory, employees, orders, customers ;

    @FXML
    private AnchorPane acMain;

    @FXML
    private AnchorPane acDashBord;

    @FXML
    private ImageView imgSellBtn;

    @FXML
    private ImageView imgSettingsBtn;

    @FXML
    private BorderPane appContent;

    @FXML
    private AnchorPane acHead;

    @FXML
    private Label staffNameLbl;

    @FXML
    private Label lblUserId;

    @FXML
    private Button btnInventory;

    @FXML
    private Button btnEmployees;

    @FXML
    private Label lblHour;

    @FXML
    private Label dateLocal1;

    @FXML
    private Label lblMinutes;

    @FXML
    private Label dateLocal11;

    @FXML
    private Label lblSeconds;

    @FXML
    private Label dateLocal;

    @FXML
    private Button btnSales;

    @FXML
    private Button btnCustomers;

    @FXML
    private Button btnLogout;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        disPlayDateTime();
        displayDate();
        try {
            employees = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Employees.fxml")));
            inventory = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Inventory.fxml")));
            orders = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Orders.fxml")));
            customers = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Customers.fxml")));

            setNode(orders);
        } catch (IOException e) {
            OrdersConnector.displayException(e);
            System.exit(1110);
        }
    }

    private void disPlayDateTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR);

            lblHour.setText(String.format("%02d", hour));
            lblMinutes.setText(String.format("%02d", minute));
            lblSeconds.setText(String.format("%02d", second));

            var username = RequestsGateway.getCurrentUser().getUsername();

            staffNameLbl.setText("Welcome " + (username == null ? "no-name" : username));
        }),
                new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public void displayDate() {
        String s;
        Format formatter;
        Date date = new Date();
        formatter = new SimpleDateFormat("dd-MMM-yyyy");
        s = formatter.format(date);
        dateLocal.setText(s);
    }

    private void setNode(Node node) {
        if (!holderPane.getChildren().isEmpty()) {
            holderPane.getChildren().clear();
        }

        holderPane.getChildren().add(node);

        var ft = new FadeTransition(Duration.millis(1000));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    @FXML
    private void btnSwitchInventory(ActionEvent ignored) {
        setNode(inventory);
    }

    @FXML
    private void btnSwitchEmployees(ActionEvent ignored) {
        setNode(employees);
    }

    @FXML
    private void btnSwitchSales(ActionEvent ignored) {
        setNode(orders);
    }

    @FXML
    private void btnSwitchCustomers(ActionEvent ignored) {
        setNode(customers);
    }

    @FXML
    private void logout(ActionEvent ignored) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("This action will close the application & " +
                "require you to reauthenticate." +
                "Any unsaved changes will be lost.");

        alert.setWidth(400);
        alert.setHeight(300);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            RequestsGateway.logout();
            System.exit(0);
        }
    }
}
