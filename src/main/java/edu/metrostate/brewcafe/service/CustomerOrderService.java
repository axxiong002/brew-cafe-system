package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.OrderStatus;

// Manages the customer workflow, such as starting order, adding items, clearing, and placing it
public class CustomerOrderService {
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private Order currentOrder;
    private int nextOrderNumber;

    public CustomerOrderService(OrderService orderService, InventoryService inventoryService) {
        this(orderService, inventoryService, 1);
    }

    public CustomerOrderService(OrderService orderService, InventoryService inventoryService, int startingOrderNumber) {
        if (orderService == null) {
            throw new IllegalArgumentException("OrderService is required.");
        }
        if (inventoryService == null) {
            throw new IllegalArgumentException("InventoryService is required.");
        }
        if (startingOrderNumber < 1) {
            throw new IllegalArgumentException("Starting order number must be at least 1.");
        }

        this.orderService = orderService;
        this.inventoryService = inventoryService;
        this.nextOrderNumber = startingOrderNumber;
    }

    public void startNewOrder(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }

        currentOrder = new Order(generateOrderId(), customerName.trim(), OrderStatus.PENDING);
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void addItemToCurrentOrder(OrderItem orderItem) {
        ensureCurrentOrderExists();

        if (orderItem == null) {
            throw new IllegalArgumentException("Order item cannot be null.");
        }

        currentOrder.addItem(orderItem);

        if (!inventoryService.isAvailable(currentOrder)) {
            currentOrder.removeItem(orderItem);
            throw new IllegalStateException("That item cannot be added because ingredients are insufficient.");
        }
    }

    public void removeItemFromCurrentOrder(OrderItem orderItem) {
        ensureCurrentOrderExists();

        if (orderItem != null) {
            currentOrder.removeItem(orderItem);
        }
    }

    public void clearCurrentOrder() {
        ensureCurrentOrderExists();
        currentOrder.clearItems();
    }

    public boolean canPlaceCurrentOrder() {
        return currentOrder != null && !currentOrder.getItems().isEmpty() && inventoryService.isAvailable(currentOrder);
    }

    public Order placeCurrentOrder() {
        ensureCurrentOrderExists();

        if (currentOrder.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an empty order.");
        }

        if (!inventoryService.isAvailable(currentOrder)) {
            throw new IllegalStateException("This order cannot be placed because ingredients are insufficient.");
        }

        boolean consumed = inventoryService.consumeForOrder(currentOrder);
        if (!consumed) {
            throw new IllegalStateException("This order cannot be placed because ingredients are insufficient.");
        }

        Order placedOrder = currentOrder;
        orderService.addOrder(placedOrder);
        currentOrder = null;
        return placedOrder;
    }

    private void ensureCurrentOrderExists() {
        if (currentOrder == null) {
            throw new IllegalStateException("No current order. Start a new order first.");
        }
    }

    private String generateOrderId() {
        String orderId = "ORD-" + String.format("%03d", nextOrderNumber);
        nextOrderNumber++;
        return orderId;
    }
}
