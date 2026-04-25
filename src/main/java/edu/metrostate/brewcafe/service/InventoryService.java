package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Ingredient;
import edu.metrostate.brewcafe.model.IngredientUsage;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Owns stock visibility, restocking, and availability checks for manager-controlled inventory.
public class InventoryService extends AbstractCafeSubject {
    private final List<Ingredient> ingredients;

    public InventoryService(List<Ingredient> ingredients) {
        this.ingredients = new ArrayList<>(ingredients);
    }

    public List<Ingredient> getAllIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public Optional<Ingredient> getIngredientById(String ingredientId) {
        return ingredients.stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .findFirst();
    }

    public boolean restockIngredient(String ingredientId, double quantityToAdd) {
        if (quantityToAdd <= 0) {
            return false;
        }

        Optional<Ingredient> ingredient = getIngredientById(ingredientId);
        if (ingredient.isEmpty()) {
            return false;
        }

        Ingredient foundIngredient = ingredient.get();
        foundIngredient.setQuantity(foundIngredient.getQuantity() + quantityToAdd);
        notifyObservers("inventory-restocked");
        return true;
    }

    public boolean setIngredientQuantity(String ingredientId, double quantity) {
        if (quantity < 0) {
            return false;
        }

        Optional<Ingredient> ingredient = getIngredientById(ingredientId);
        if (ingredient.isEmpty()) {
            return false;
        }

        ingredient.get().setQuantity(quantity);
        notifyObservers("inventory-quantity-updated");
        return true;
    }

    public boolean isAvailable(MenuItem menuItem) {
        for (IngredientUsage usage : menuItem.getIngredientUsages()) {
            Optional<Ingredient> ingredient = getIngredientById(usage.ingredientId());
            if (ingredient.isEmpty() || ingredient.get().getQuantity() < usage.quantityRequired()) {
                return false;
            }
        }

        return true;
    }

    public boolean consumeForMenuItem(MenuItem menuItem) {
        if (!isAvailable(menuItem)) {
            return false;
        }

        for (IngredientUsage usage : menuItem.getIngredientUsages()) {
            Ingredient ingredient = getIngredientById(usage.ingredientId()).orElseThrow();
            ingredient.setQuantity(ingredient.getQuantity() - usage.quantityRequired());
        }

        notifyObservers("inventory-consumed");
        return true;
    }

    // Added for customer side
    public boolean isAvailable(OrderItem orderItem) {
        if (orderItem == null || orderItem.getMenuItem() == null) {
            return false;
        }

        for (IngredientUsage usage : orderItem.getMenuItem().getIngredientUsages()) {
            Optional<Ingredient> ingredient = getIngredientById(usage.ingredientId());
            double required = usage.quantityRequired() * orderItem.getQuantity();

            if (ingredient.isEmpty() || ingredient.get().getQuantity() < required) {
                return false;
            }
        }

        return true;
    }

    // Added for customer side
    public boolean isAvailable(Order order) {
        if (order == null) {
            return false;
        }

        Map<String, Double> requiredTotals = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            if (item == null || item.getMenuItem() == null) {
                return false;
            }

            for (IngredientUsage usage : item.getMenuItem().getIngredientUsages()) {
                double required = usage.quantityRequired() * item.getQuantity();
                requiredTotals.merge(usage.ingredientId(), required, Double::sum);
            }
        }

        for (Map.Entry<String, Double> entry : requiredTotals.entrySet()) {
            Optional<Ingredient> ingredient = getIngredientById(entry.getKey());
            if (ingredient.isEmpty() || ingredient.get().getQuantity() < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    // Added for customer side
    public boolean consumeForOrder(Order order) {
        if (!isAvailable(order)) {
            return false;
        }

        Map<String, Double> requiredTotals = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            for (IngredientUsage usage : item.getMenuItem().getIngredientUsages()) {
                double required = usage.quantityRequired() * item.getQuantity();
                requiredTotals.merge(usage.ingredientId(), required, Double::sum);
            }
        }

        for (Map.Entry<String, Double> entry : requiredTotals.entrySet()) {
            Ingredient ingredient = getIngredientById(entry.getKey()).orElseThrow();
            ingredient.setQuantity(ingredient.getQuantity() - entry.getValue());
        }

        notifyObservers("inventory-consumed");
        return true;
    }
}
