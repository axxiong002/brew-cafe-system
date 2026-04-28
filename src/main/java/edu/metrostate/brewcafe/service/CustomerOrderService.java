package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.OrderStatus;
import edu.metrostate.brewcafe.model.SizeOption;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// Owns the customer's draft order before it is submitted to the shared barista queue.
public class CustomerOrderService {
    private final CafeApplicationState applicationState;
    private Order currentOrder;
    private int nextOrderNumber;

    public CustomerOrderService(CafeApplicationState applicationState) {
        this.applicationState = applicationState;
        nextOrderNumber = applicationState.getOrderService().getPendingOrders().size()
                + applicationState.getOrderService().getFulfilledOrders().size()
                + 1;
    }

    public void startNewOrder(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }

        currentOrder = new Order(generateOrderId(), customerName.trim(), OrderStatus.PENDING);
    }

    public Optional<Order> getCurrentOrder() {
        return Optional.ofNullable(currentOrder);
    }

    public OrderItem addItemToCurrentOrder(
            MenuItem menuItem,
            int quantity,
            SizeOption selectedSize,
            List<Customization> selectedCustomizations
    ) {
        ensureActiveOrder();
        validateOrderItem(menuItem, quantity);

        OrderItem orderItem = new OrderItem(menuItem, quantity, selectedSize, selectedCustomizations);
        currentOrder.addItem(orderItem);
        return orderItem;
    }

    public void removeItemFromCurrentOrder(OrderItem orderItem) {
        ensureActiveOrder();
        currentOrder.removeItem(orderItem);
    }

    public void clearCurrentOrder() {
        ensureActiveOrder();
        currentOrder.clearItems();
    }

    public boolean canPlaceCurrentOrder() {
        return currentOrder != null
                && !currentOrder.getItems().isEmpty()
                && applicationState.getInventoryService().canFulfillOrder(currentOrder);
    }

    public Order placeCurrentOrder() throws IOException {
        ensureActiveOrder();
        if (currentOrder.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an empty order.");
        }

        if (!applicationState.getInventoryService().consumeForOrder(currentOrder)) {
            throw new IllegalStateException("Cannot place order because inventory is too low.");
        }

        Order placedOrder = currentOrder;
        applicationState.getOrderService().addOrder(placedOrder);
        applicationState.saveInventoryData();
        applicationState.saveOrderData();
        currentOrder = null;
        return placedOrder;
    }

    private void ensureActiveOrder() {
        if (currentOrder == null) {
            throw new IllegalStateException("Start a customer order first.");
        }
    }

    private void validateOrderItem(MenuItem menuItem, int quantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item is required.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
    }

    private String generateOrderId() {
        return "ORDER-" + String.format("%03d", nextOrderNumber++);
    }
}