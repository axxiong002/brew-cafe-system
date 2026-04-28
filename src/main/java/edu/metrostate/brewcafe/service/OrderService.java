package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Keeps order queue behavior in one place and provides a base for live updates later.
public class OrderService extends AbstractCafeSubject {
    private final List<Order> pendingOrders = new ArrayList<>();
    private final List<Order> fulfilledOrders = new ArrayList<>();

    public OrderService() {
    }

    public OrderService(List<Order> pendingOrders, List<Order> fulfilledOrders) {
        this.pendingOrders.addAll(pendingOrders);
        this.fulfilledOrders.addAll(fulfilledOrders);
    }

    public void addOrder(Order order) {
        // Keeping queue behavior in a service helps prevent order logic from leaking into the UI.
        order.setStatus(OrderStatus.PENDING);
        pendingOrders.add(order);
        notifyObservers("order-added");
    }

    public List<Order> getPendingOrders() {
        return Collections.unmodifiableList(pendingOrders);
    }

    public List<Order> getFulfilledOrders() {
        return Collections.unmodifiableList(fulfilledOrders);
    }

    public Optional<Order> getNextPendingOrder() {
        if (pendingOrders.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(pendingOrders.getFirst());
    }

    public Optional<Order> getPendingOrderById(String orderId) {
        return pendingOrders.stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst();
    }

    public boolean updateOrderStatus(String orderId, OrderStatus status) {
        for (Order order : pendingOrders) {
            if (order.getId().equals(orderId)) {
                order.setStatus(status);
                notifyObservers("order-status-updated");
                return true;
            }
        }

        return false;
    }

    public boolean completeOrder(String orderId) {
        for (Order order : new ArrayList<>(pendingOrders)) {
            if (order.getId().equals(orderId)) {
                pendingOrders.remove(order);
                order.setStatus(OrderStatus.FULFILLED);
                fulfilledOrders.add(order);
                notifyObservers("order-fulfilled");
                return true;
            }
        }

        return false;
    }
}
