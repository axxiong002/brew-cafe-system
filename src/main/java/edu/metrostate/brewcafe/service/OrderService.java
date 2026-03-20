package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Keeps order queue behavior in one place and provides a base for live updates later.
public class OrderService extends AbstractCafeSubject {
    private final List<Order> pendingOrders = new ArrayList<>();

    public void addOrder(Order order) {
        // Keeping queue behavior in a service helps prevent order logic from leaking into the UI.
        pendingOrders.add(order);
        notifyObservers("order-added");
    }

    public List<Order> getPendingOrders() {
        return Collections.unmodifiableList(pendingOrders);
    }
}
