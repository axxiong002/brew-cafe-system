package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;

public class OrderPricingService {
	
	/**
	 * Calculates the adjustments of size and customizations(or if none, base price)
	 * @param orderItem
	 * @return Added price of adjustment(if any) with base price
	 */
	public double calculateUnitPrice(OrderItem orderItem) {
		if (orderItem == null || orderItem.getMenuItem() == null)
			throw new IllegalArgumentException("Order item and menu item are required.");
		
		double price = orderItem.getMenuItem().getBasePrice();
		if (orderItem.getSelectedSize() != null)
			price += orderItem.getSelectedSize().priceAdjustment();

		for (Customization customization : orderItem.getSelectedCustomizations())
			price += customization.extraCost();
		
		return price;
	}
	
	/**
	 * Calculates the adjusted augmentations with Quantity
	 * @param orderItem
	 * @return Unit Price * quantity
	 */
	public double calculateLineTotal(OrderItem orderItem) {
		if (orderItem == null)
			throw new IllegalArgumentException("Order item is required.");
		
		return calculateUnitPrice(orderItem) * orderItem.getQuantity();
	}
	
	/**
	 * Calculate total
	 * @param order
	 * @return Total Order cost
	 */
	public double calculateOrderTotal(Order order) {
		if (order == null)
			throw new IllegalArgumentException("Order is required.");
		
		double total = 0.0;
		for (OrderItem item : order.getItems())
			total += calculateLineTotal(item);
		
		return total;
	}
}
