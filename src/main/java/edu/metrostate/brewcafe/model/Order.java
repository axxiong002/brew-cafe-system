package edu.metrostate.brewcafe.model;

import java.util.ArrayList;
import java.util.List;

// Basic order model for customer info, ordered items, and fulfillment status.
public class Order {
    private final String id;
    private final String customerName;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;

    public Order(String id, String customerName, OrderStatus status) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
