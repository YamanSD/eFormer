package eformer.back.eformer_backend.model;

import eformer.back.eformer_backend.utility.NegativeQuantityException;
import jakarta.persistence.*;

import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private final Integer itemId;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "introduction_date")
    @Temporal(TemporalType.TIMESTAMP)
    private final Timestamp introductionDate;

    protected Item(Integer itemId, String name, String description,
                Integer quantity, Double unitPrice, Timestamp introductionDate,
                   Double cost) {
        this.itemId = itemId;
        this.introductionDate = introductionDate;
        setName(name);
        setDescription(description);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
        setCost(cost);
    }

    public Item(String name, String description,
                Integer quantity, Double unitPrice, Double cost) {
        this(-1, name, description, quantity,
                unitPrice, new Timestamp(new Date().getTime()),
                cost);
    }

    public Item() {
        this("", "", 0, 0.0, 0.0);
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setCost(Integer cost) {
        setCost(cost.doubleValue());
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

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        setUnitPrice(unitPrice.doubleValue());
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getIntroductionDate() {
        return introductionDate.toLocalDateTime();
    }

    public void addQuantity(Integer quantity) {
        if (quantity < 0 && -quantity > getQuantity()) {
            throw new NegativeQuantityException();
        }

        this.quantity += quantity;
    }

    public boolean removeQuantity(Integer quantity) {
        if (getQuantity() < quantity) {
            return false;
        }

        this.quantity -= quantity;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return getItemId().equals(item.getItemId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemId());
    }
}