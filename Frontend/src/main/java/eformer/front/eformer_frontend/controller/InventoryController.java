package eformer.front.eformer_frontend.controller;

import eformer.front.eformer_frontend.Main;
import eformer.front.eformer_frontend.connector.ItemsConnector;
import eformer.front.eformer_frontend.connector.OrdersConnector;
import eformer.front.eformer_frontend.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;


public class InventoryController implements Initializable {

    @FXML
    private AnchorPane acContent;

    @FXML
    private BorderPane bpRoot;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnUpdate;

    @FXML
    private SplitPane splitPane;

    @FXML
    private TextArea taDescription;

    @FXML
    private TableColumn<?, ?> tblClmCostPrice;

    @FXML
    private TableColumn<?, ?> tblClmDescription;

    @FXML
    private TableColumn<?, ?> tblClmItemId;

    @FXML
    private TableColumn<?, ?> tblClmItemName;

    @FXML
    private TableColumn<?, ?> tblClmQuantity;

    @FXML
    private TableColumn<?, ?> tblClmUnitPrice;

    @FXML
    private TableView<Item> tblInventory;

    @FXML
    private TextField tfCostPrice;

    @FXML
    private TextField tfItemName;

    @FXML
    private TextField tfQuantity;

    @FXML
    private TextField tfSearch;

    @FXML
    private TextField tfUnitPrice;

    private final ObservableList<Item> items = FXCollections.observableArrayList();

    private Item currentSelectedItem = null;

    public void activateTableFunctionalities() {
        /* Once an item is selected display its properties */
        tblInventory.getSelectionModel().selectedItemProperty().addListener((observable, oldSelected, selected) -> {
            if (selected != null) {
                setFields(selected);
                currentSelectedItem = selected;
            }
        });

        /* Search item by name */
        tblInventory.getItems()
                .stream()
                .filter(item -> item != null &&
                        tfSearch.getLength() != 0 &&
                        item.getName().contains(tfSearch.getText()))
                .findAny()
                .ifPresent(item -> {
                    tblInventory.getSelectionModel().select(item);
                    tblInventory.scrollTo(item);
        });
    }

    public void refresh(ActionEvent ignored) {
        refreshTable();
    }

    public void refreshTable() {
        items.clear();
        items.addAll(Objects.requireNonNull(ItemsConnector.getAll()));
        clearFields();
        tblInventory.setItems(items);
    }

    public void setFields(Item item) {
        if (currentSelectedItem == item) {
            clearFields();
            return;
        }

        tfCostPrice.setText(String.format("$%.2f", item.getCost()));
        tfQuantity.setText(item.getQuantity().toString());
        tfItemName.setText(item.getName());
        tfUnitPrice.setText(String.format("$%.2f", item.getUnitPrice()));
        taDescription.setText(item.getDescription());
    }

    public void clearFields() {
        tfCostPrice.clear();
        tfQuantity.clear();
        tfItemName.clear();
        tfUnitPrice.clear();
        taDescription.clear();
        tfSearch.clear();
        currentSelectedItem = null;
    }

    public Item fetchItemFromFields() {
        var costStr = '$' + tfCostPrice.getText();
        Double cost = Double.parseDouble(costStr.substring(costStr.lastIndexOf('$') + 1));

        Integer quantity = Integer.parseInt(tfQuantity.getText());
        var name = tfItemName.getText();

        var unitPriceStr = '$' + tfUnitPrice.getText();
        Double unitPrice = Double.parseDouble(unitPriceStr.substring(unitPriceStr.lastIndexOf('$') + 1));

        var description = taDescription.getText();

        return new Item(name, description, quantity, unitPrice, cost);
    }

    public void btnCancelAction(ActionEvent ignored) {
        if (currentSelectedItem == null) {
            OrdersConnector.displayWarning("Invalid action", "No previous item");
        } else {
            setFields(currentSelectedItem);
        }
    }

    public void btnUpdateAction(ActionEvent ignored) {
        var fieldsItem = fetchItemFromFields();

        if (currentSelectedItem != null) {
            fieldsItem.setItemId(currentSelectedItem.getItemId());

            var item = ItemsConnector.update(fieldsItem);

            if (item != null) {
                items.set(items.indexOf(currentSelectedItem), item);
                currentSelectedItem = item;
            }
        } else {
            var item = ItemsConnector.create(fieldsItem);
            items.add(item);
            currentSelectedItem = item;
        }

        tblInventory.setItems(items);
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
        tblClmItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        tblClmDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tblClmItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblClmQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblClmCostPrice.setCellValueFactory(new PropertyValueFactory<>("cost"));
        tblClmUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        refreshTable();
        activateTableFunctionalities();
    }
}

