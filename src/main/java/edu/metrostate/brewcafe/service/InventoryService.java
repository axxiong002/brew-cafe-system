package edu.metrostate.brewcafe.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.metrostate.brewcafe.model.Ingredient;
import edu.metrostate.brewcafe.model.IngredientUsage;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.persistence.JsonDataLoader;

public class InventoryService {
    private final JsonDataLoader jsonDataLoader;
    private final Path inventoryFilePath;
    private final Map<String, Ingredient> ingredientsById = new HashMap<>();
    
    public InventoryService(JsonDataLoader jsonDataLoader, Path inventoryFilePath) {
    	if (jsonDataLoader == null)
    		throw new IllegalArgumentException("JsonDataLoader is required.");
    	if (inventoryFilePath == null)
    		throw new IllegalArgumentException("Inventory file path is required.");
    	
    	this.jsonDataLoader = jsonDataLoader;
    	this.inventoryFilePath = inventoryFilePath;
    }
    
    public void loadInventory() throws IOException {
        ingredientsById.clear();

        List<Ingredient> ingredients = jsonDataLoader.loadIngredients(inventoryFilePath);
        for (Ingredient ingredient : ingredients) {
            ingredientsById.put(ingredient.getId(), ingredient);
        }
    }
    
    public boolean canMakeOrderItem(OrderItem orderItem) {
        if (orderItem == null || orderItem.getMenuItem() == null)
            return false;

        for (IngredientUsage usage : orderItem.getMenuItem().getIngredientUsages()) {
            Ingredient ingredient = ingredientsById.get(usage.ingredientId());
            if (ingredient == null)
                return false;

            double required = usage.quantityRequired() * orderItem.getQuantity();
            
            if (ingredient.getQuantity() < required) 
                return false;
        }
        return true;
    }
    
    public boolean canMakeOrder(Order order) {
        if (order == null) 
            return false;

        Map<String, Double> total = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            if (item == null || item.getMenuItem() == null)
                return false;

            for (IngredientUsage usage : item.getMenuItem().getIngredientUsages()) {
                double required = usage.quantityRequired() * item.getQuantity();
                total.merge(usage.ingredientId(), required, Double::sum);
            }
        }

        for (Map.Entry<String, Double> entry : total.entrySet()) {
            Ingredient ingredient = ingredientsById.get(entry.getKey());
            if (ingredient == null || ingredient.getQuantity() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    public void deductIngredientsForOrder(Order order) {
        if (!canMakeOrder(order)) {
            throw new IllegalStateException("Not enough ingredients to complete this order.");
        }

        for (OrderItem item : order.getItems()) {
            for (IngredientUsage usage : item.getMenuItem().getIngredientUsages()) {
                Ingredient ingredient = ingredientsById.get(usage.ingredientId());
                double required = usage.quantityRequired() * item.getQuantity();
                ingredient.setQuantity(ingredient.getQuantity() - required);
            }
        }
    }
}
