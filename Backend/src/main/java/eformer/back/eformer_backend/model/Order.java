package eformer.back.eformer_backend.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eformer.back.eformer_backend.utility.InvalidOrderUpdateException;
import eformer.back.eformer_backend.utility.OrderCannotChangeException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import eformer.back.eformer_backend.model.keys.OrderItemId;
import eformer.back.eformer_backend.repository.ItemRepository;
import eformer.back.eformer_backend.repository.OrderItemsRepository;
import eformer.back.eformer_backend.utility.NegativeQuantityException;
import org.springframework.transaction.annotation.Transactional;


@Entity
@Table(name = "orders")
@Transactional
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private final Integer orderId;

    @Column(name = "total")
    private Double total;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private final Timestamp creationDate;

    @Column(name = "number_of_items")
    private Integer numberOfItems;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Column(name = "status")
    private String status;

    @ManyToOne
    private User customer;

    @ManyToOne
    private User employee;

    @Column(name = "note")
    private String note;

    @Column(name = "profit")
    private Double profit;

    @Transient
    @JsonIgnore
    public static OrderItemsRepository orderItemsManager;

    @Transient
    @JsonIgnore
    public static ItemRepository itemsManager;

    protected Order(Integer orderId, Double total, Timestamp creationDate,
                    Integer numberOfItems, Double amountPaid,
                    String status, User customer,
                    User employee, String note,
                    Double profit) {
        this.orderId = orderId;
        this.creationDate = creationDate;
        this.numberOfItems = numberOfItems;
        setTotal(total);
        setAmountPaid(amountPaid);
        setStatus(status);
        setCustomer(customer);
        setEmployee(employee);
        setNote(note);
        setProfit(profit);
    }

    public Order() {
        this(null, null);
    }

    public Order(User customer, User employee) {
        this(-1, 0.0, new Timestamp(new Date().getTime()), 0,
                0.0, "Pending", customer, employee, "", 0.0);
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getTotal() {
        return total;
    }

    private void setTotal(Double total) {
        this.total = total;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate.toLocalDateTime();
    }

    public Integer getNumberOfItems() {
        return numberOfItems;
    }

    public static void setOrderItemsManager(OrderItemsRepository manager) {
        orderItemsManager = manager;
    }

    public static void setItemsManager(ItemRepository manager) {
        itemsManager = manager;
    }

    public void addToOrder(Integer itemId, Integer quantity) {
        editItem(itemId, quantity);
    }

    public boolean isCanceled() {
        return getStatus().equals("Cancelled");
    }

    public boolean isConfirmed() {
        return getStatus().equals("Confirmed");
    }

    public boolean isPending() {
        return getStatus().equals("Pending");
    }

    public boolean areValidItems(HashMap<String, Integer> items) {
        for (var itemId: items.keySet()) {
            var item = itemsManager.findById(Integer.parseInt(itemId));

            if (item.isEmpty()) {
                return false;
            }

            var orderItem = orderItemsManager.findById(new OrderItemId(item.get().getItemId(), getOrderId()));

            if (orderItem.isEmpty() &&
                    item.get().getQuantity() < items.get(itemId)) {
                return false;
            } else if (orderItem.isPresent() &&
                    item.get().getQuantity() + orderItem.get().getQuantity() < items.get(itemId)) {
                return false;
            }
        }

        return true;
    }

    public void editItem(Integer itemId, Integer removedQuantity) {
        if (!isPending()) {
            throw new OrderCannotChangeException("Order is " + getStatus());
        }

        try {
            var orderItem = orderItemsManager.findById(new OrderItemId(itemId, getOrderId()));
            var item = itemsManager.findById(itemId).orElseThrow();

            if (orderItem.isPresent() && removedQuantity < 0) {
                numberOfItems += removedQuantity;
                total += removedQuantity * item.getUnitPrice();
                profit += removedQuantity * (item.getUnitPrice() - item.getCost());

                orderItem.get().addQuantity(removedQuantity);

                /* Remove if quantity is zero */
                if (orderItem.get().getQuantity() == 0) {
                    orderItemsManager.delete(orderItem.get());
                } else {
                    orderItemsManager.save(orderItem.get());
                }
            } else if (removedQuantity > 0 && item.getQuantity() >= removedQuantity) {
                numberOfItems += removedQuantity;
                total += item.getUnitPrice() * removedQuantity;
                profit += removedQuantity * (item.getUnitPrice() - item.getCost());

                orderItemsManager.save(new OrderItem(this, item, removedQuantity));
            } else {
                throw new NegativeQuantityException();
            }
        } catch (NegativeQuantityException e) {
            orderItemsManager.deleteAllByOrder(this);
            throw e;
        }
    }

    public void addItems(HashMap<String, Integer> items) {
        for (var itemId: items.keySet()) {
            addToOrder(Integer.parseInt(itemId), items.get(itemId));
        }
    }

    public void setItems(HashMap<String, Integer> items) {
        if (isConfirmed() || isCanceled()) {
            throw new InvalidOrderUpdateException("Order already " + getStatus());
        } else if (!areValidItems(items)) {
            throw new InvalidOrderUpdateException("Quantities too large");
        }

        numberOfItems = 0;
        total = 0.0;
        orderItemsManager.deleteAllByOrder(this);
        addItems(items);
    }

    public void confirm(Double amountPaid) {
        if (!isPending()) {
            throw new InvalidOrderUpdateException("Order is " + getStatus());
        }

        var orderItems = orderItemsManager.findAllByOrder(this);
        var items = new ArrayList<Item>();

        for (var orderItem: orderItems) {
            var item = orderItem.getItem();

            item.removeQuantity(orderItem.getQuantity());
            items.add(item);
        }

        setAmountPaid(amountPaid);
        itemsManager.saveAll(items);
        setStatus("Confirmed");
    }

    public void returnItems() {
        var orderItems = orderItemsManager.findAllByOrder(this);

        for (var orderItem: orderItems) {
            var item = orderItem.getItem();

            item.addQuantity(orderItem.getQuantity());

            itemsManager.save(item);
        }
    }

    public void cancel() {
        if (isCanceled()) {
            throw new InvalidOrderUpdateException("Order already " + getStatus());
        } else if (isConfirmed()) {
            returnItems();
        }

        setProfit(0.0);
        numberOfItems = 0;
        orderItemsManager.deleteAllByOrder(this);
        setStatus("Cancelled");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equals(getOrderId(), order.getOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderId());
    }
}