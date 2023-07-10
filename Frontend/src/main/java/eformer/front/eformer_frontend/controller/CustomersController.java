package eformer.front.eformer_frontend.controller;

import eformer.front.eformer_frontend.connector.UsersConnector;
import eformer.front.eformer_frontend.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {

    @FXML
    private AnchorPane acContent;

    @FXML
    private AnchorPane bpRightAnchor;

    @FXML
    private BorderPane bpRoot;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnUpdate;

    @FXML
    private ComboBox<String> cbRole;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private SplitPane splitPane;

    @FXML
    private TableColumn<?, ?> tblClmEmail;

    @FXML
    private TableColumn<?, ?> tblClmFullName;

    @FXML
    private TableColumn<?, ?> tblClmIEmployeeNumber;

    @FXML
    private TableColumn<?, ?> tblClmJoin;

    @FXML
    private TableColumn<?, ?> tblClmUsername;

    @FXML
    private TableView<User> tblCustomers;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfFullName;

    @FXML
    private TextField tfSearch;

    @FXML
    private TextField tfUsername;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    private User currentSelectedUser = null;

    public void activateTableFunctionalities() {
        /* Search users by name */
        tblCustomers.getItems()
                .stream()
                .filter(item -> item != null &&
                        tfSearch.getLength() != 0 &&
                        item.getUsername().contains(tfSearch.getText()))
                .findAny()
                .ifPresent(item -> {
                    tblCustomers.getSelectionModel().select(item);
                    tblCustomers.scrollTo(item);
        });

        /* Once a user is selected display its properties */
        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldSelected, selected) -> {
            if (selected != null) {
                setFields(selected);
                currentSelectedUser = selected;
            }
        });
    }

    public void setFields(User user) {
        if (currentSelectedUser == user) {
            clearFields();
            return;
        }

        tfEmail.setText(user.getEmail());
        tfFullName.setText(user.getFullName());
        tfUsername.setText(user.getUsername());
        cbRole.setValue(user.getRole());
        pfPassword.setText(user.getPassword());
    }

    public void clearFields() {
        tfSearch.clear();
        tfEmail.clear();
        tfFullName.clear();
        tfUsername.clear();
        cbRole.setValue(null);
        pfPassword.clear();
        currentSelectedUser = null;
    }

    public void refreshTable() {
        users.clear();
        users.addAll(Objects.requireNonNull(UsersConnector.getCustomers()));
        clearFields();
        tblCustomers.setItems(users);
    }

    public User fetchUserFromFields() {
        Integer adLevel = Objects.requireNonNull(UsersConnector.roles()).indexOf(cbRole.getValue()) - 1;
        String username = tfUsername.getText();
        String fullName = tfFullName.getText();
        String password = pfPassword.getText();
        String email = tfEmail.getText();

        return new User(username, fullName, email, password, adLevel);
    }

    public void refresh(ActionEvent ignored) {
        refreshTable();
    }

    public void btnCancelAction(ActionEvent ignored) {
        if (currentSelectedUser == null) {
            UsersConnector.displayWarning("Invalid action", "No previous customer");
        } else {
            setFields(currentSelectedUser);
        }
    }

    public void btnUpdateAction(ActionEvent ignored) {
        var fieldsUser = fetchUserFromFields();

        if (currentSelectedUser != null) {
            fieldsUser.setUserId(currentSelectedUser.getUserId());

            var response = UsersConnector.update(fieldsUser);

            if (response) {
                if (fieldsUser.isCustomer()) {
                    users.set(users.indexOf(currentSelectedUser), fieldsUser);
                    currentSelectedUser = fieldsUser;
                } else {
                    refreshTable();
                }
            }
        } else {
            var user = UsersConnector.create(fieldsUser);
            users.add(user);
            currentSelectedUser = user;
        }

        tblCustomers.setItems(users);
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tblClmEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tblClmFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tblClmUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tblClmJoin.setCellValueFactory(new PropertyValueFactory<>("joinDate"));
        tblClmIEmployeeNumber.setCellValueFactory(new PropertyValueFactory<>("userId"));

        cbRole.setItems(FXCollections.observableArrayList(Objects.requireNonNull(UsersConnector.roles())));
        refreshTable();
        activateTableFunctionalities();
    }
}

