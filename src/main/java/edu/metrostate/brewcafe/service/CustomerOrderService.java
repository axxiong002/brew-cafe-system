package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.OrderStatus;

/**
 * 	Starts new order for customer, hold current draft order, add/remove items from draft order
 * 	clears draft order, and sends draft order to finalized order
 */
public class CustomerOrderService {
	private final OrderService orderService;
	private final InventoryService inventoryService;
	private Order currentOrder;
	private int nextOrderId = 1;
	
	public CustomerOrderService(OrderService orderService, InventoryService inventoryService) {
		if (orderService == null)
			throw new IllegalArgumentException("OrderService is required.");
		if (inventoryService == null)
			throw new IllegalArgumentException("InventoryService is required.");
		
		this.orderService = orderService;
		this.inventoryService = inventoryService;
	}
	
	public void startNewOrder(String customerName) {
		if (customerName == null || customerName.isBlank())
			throw new IllegalArgumentException("Name is required.");
		
		currentOrder = new Order(generateOrderId(), customerName.trim(), OrderStatus.PENDING);	// Switch when completed
	}
	
	public Order getCurrentOrder() {
		return currentOrder;
	}
	
	public void addItemToCurrentOrder(OrderItem orderItem) {
        if (currentOrder == null)
            throw new IllegalStateException("No active order. Start a new order first.");
        if (orderItem == null)
        	throw new IllegalArgumentException("Order item cannot be null.");
        
        currentOrder.addItem(orderItem);
        
        if (!inventoryService.canMakeOrder(currentOrder)) {
        	currentOrder.removeItem(orderItem);
        	throw new IllegalStateException("That item cannot be added because ingredients are insufficient.");
        }
	}
	
    public void removeItemFromCurrentOrder(OrderItem orderItem) {
        if (currentOrder == null)
        	throw new IllegalStateException("No active order. Start a new order first.");
        
        if (orderItem != null)
            currentOrder.removeItem(orderItem);
    }

    public void clearCurrentOrder() {
    	if (currentOrder == null)
    		throw new IllegalStateException("No active order. Start a new order first.");
        
    	currentOrder.clearItems();
    }
    
    /**
     * 	Checks if order has items
     *  @return Whether order can be placed.
     */
    public boolean canPlaceCurrentOrder() {
        return currentOrder != null && !currentOrder.getItems().isEmpty();
    }

    /**
     * Validates if current order has any menu items then places the order
     * @return Sends the draft order to OrderService
     */
    public Order placeCurrentOrder() {
        if (currentOrder  == null)
        	throw new IllegalStateException("No active order. Start a new order first.");
        if (currentOrder.getItems().isEmpty())
            throw new IllegalStateException("Cannot place an empty order.");

        Order placedOrder = currentOrder;
        inventoryService.deductIngredientsForOrder(placedOrder);
        orderService.addOrder(placedOrder);
        currentOrder = null;
        return placedOrder;
    }
    /**
     * Format and creates the Order Id and increment
     * @return the orderId
     */
    private String generateOrderId() {
    	String orderId = "ORDER-" + String.format("%03d", nextOrderId);
    	nextOrderId++;
    	return orderId;
    }
}
