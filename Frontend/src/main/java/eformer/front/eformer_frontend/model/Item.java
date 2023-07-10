package eformer.front.eformer_frontend.model;

import eformer.front.eformer_frontend.connector.ItemsConnector;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Item {
    private Integer itemId;

    private String name;

    private String description;

    private Integer quantity;

    private Double unitPrice;

    private LocalDateTime introductionDate;

    private Double cost;

    private Integer requestedQuantity = 0;

    public Item(JSONObject json) {
        ItemsConnector.mapToObject(json, this);
    }

    public Item(String name, String description,
                Integer quantity, Double unitPrice,
                Double cost) {
        setName(name);
        setDescription(description);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
        setCost(cost);
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Double getCost() {
        return Math.floor(cost * 100) / 100;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setCost(BigDecimal cost) {
        setCost(cost.doubleValue());
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return Math.floor(unitPrice * 100) / 100;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        setUnitPrice(unitPrice.doubleValue());
    }

    public LocalDateTime getIntroductionDate() {
        return introductionDate;
    }

    public void setIntroductionDate(LocalDateTime introductionDate) {
        this.introductionDate = introductionDate;
    }

    public void setIntroductionDate(String introductionDate) {
        setIntroductionDate(LocalDateTime.parse(introductionDate));
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }
}
