package eformer.front.eformer_frontend.model;

import eformer.front.eformer_frontend.connector.OrdersConnector;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Integer orderId;

    private Double total;

    private LocalDateTime creationDate;

    private Integer numberOfItems;

    private Double amountPaid;

    private String status;

    private User customer;

    private User employee;

    private String note;

    private Double profit;

    public Order(User customer, User employee) {
        setCustomer(customer);
        setEmployee(employee);
    }

    public Order(JSONObject json) {
        OrdersConnector.mapToObject(json, this);
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public void setProfit(BigDecimal profit) {
        setProfit(profit.doubleValue());
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Double getTotal() {
        return Math.floor(total * 100) / 100;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreationDate(String creationDate) {
        setCreationDate(LocalDateTime.parse(creationDate));
    }

    public Integer getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public Double getAmountPaid() {
        return amountPaid == null ? 0 : Math.floor(amountPaid * 100) / 100;
    }

    public String getCustomerUsername() {
        return getCustomer().getUsername();
    }

    public String getEmployeeUsername() {
        return getEmployee().getUsername();
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

    public void setAmountPaid(BigDecimal amountPaid) {
        setAmountPaid(amountPaid.doubleValue());
    }

    public void setTotal(BigDecimal total) {
        setTotal(total.doubleValue());
    }

    public void setCustomer(JSONObject customer) {
        setCustomer(new User(customer));
    }

    public void setEmployee(JSONObject employee) {
        setEmployee(new User(employee));
    }

    public String getDate() {
        var temp = getCreationDate().toString().split("T");

        return temp[0] + "\n at " + temp[1];
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }
}
