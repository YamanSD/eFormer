package eformer.front.eformer_frontend.controller;

import eformer.front.eformer_frontend.connector.ItemsConnector;
import eformer.front.eformer_frontend.connector.UsersConnector;
import eformer.front.eformer_frontend.model.Item;
import eformer.front.eformer_frontend.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.json.JSONObject;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeesController implements Initializable {
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
    private TableColumn<?, ?> tblClmRole;

    @FXML
    private TableColumn<?, ?> tblClmUsername;

    @FXML
    private TableView<User> tblEmployees;

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
        /* Once a user is selected display its properties */
        tblEmployees.getSelectionModel().selectedItemProperty().addListener((observable, oldSelected, selected) -> {
            if (selected != null) {
                setFields(selected);
                currentSelectedUser = selected;
            }
        });

        /* Search users by name */
        tblEmployees.getItems()
                .stream()
                .filter(item -> item != null &&
                        tfSearch.getLength() != 0 &&
                        item.getUsername().contains(tfSearch.getText()))
                .findAny()
                .ifPresent(item -> {
                    tblEmployees.getSelectionModel().select(item);
                    tblEmployees.scrollTo(item);
                });
    }

    public void setFields(User user) {
        if (fetchUserFromFields() == user) {
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
        users.addAll(Objects.requireNonNull(UsersConnector.getEmployees()));
        clearFields();
        tblEmployees.setItems(users);
    }

    public User fetchUserFromFields() {
        String username = tfUsername.getText();
        String fullName = tfFullName.getText();
        String password = pfPassword.getText();
        String email = tfEmail.getText();
        Integer adLevel = Objects.requireNonNull(UsersConnector.roles())
                .indexOf(cbRole.getValue()) - 1;

        return new User(username, fullName, email, password, adLevel);
    }

    public void refresh(ActionEvent ignored) {
        refreshTable();
    }

    public void btnCancelAction(ActionEvent ignored) {
        setFields(currentSelectedUser);
    }

    public void btnUpdateAction(ActionEvent ignored) {
        var fieldsUser = fetchUserFromFields();

        if (currentSelectedUser != null) {
            fieldsUser.setUserId(currentSelectedUser.getUserId());

            var response = UsersConnector.update(fieldsUser);

            if (response) {
                if (fieldsUser.isEmployee()) {
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

        tblEmployees.setItems(users);
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
        tblClmRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        tblClmIEmployeeNumber.setCellValueFactory(new PropertyValueFactory<>("userId"));

        cbRole.setItems(FXCollections.observableArrayList(Objects.requireNonNull(UsersConnector.roles())));
        refreshTable();
        activateTableFunctionalities();
    }
}

