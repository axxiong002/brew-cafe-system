package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;

// Centralizes order price calculations so customer and barista views show the same totals.
public class OrderPricingService {
    public double calculateUnitPrice(OrderItem orderItem) {
        if (orderItem == null || orderItem.getMenuItem() == null) {
            throw new IllegalArgumentException("Order item and menu item are required.");
        }

        double price = orderItem.getMenuItem().getBasePrice();
        if (orderItem.getSelectedSize() != null) {
            price += orderItem.getSelectedSize().priceAdjustment();
        }

        for (Customization customization : orderItem.getSelectedCustomizations()) {
            price += customization.extraCost();
        }

        return price;
    }

    public double calculateLineTotal(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("Order item is required.");
        }

        return calculateUnitPrice(orderItem) * orderItem.getQuantity();
    }

    public double calculateOrderTotal(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order is required.");
        }

        return order.getItems().stream()
                .mapToDouble(this::calculateLineTotal)
                .sum();
    }
}