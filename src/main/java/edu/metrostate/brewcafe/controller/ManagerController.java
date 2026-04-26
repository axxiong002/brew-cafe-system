package edu.metrostate.brewcafe.controller;

import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.Ingredient;
import edu.metrostate.brewcafe.model.IngredientUsage;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Pastry;
import edu.metrostate.brewcafe.model.SizeOption;
import edu.metrostate.brewcafe.model.UserRole;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.service.MenuItemFactory;
import edu.metrostate.brewcafe.view.ManagerDashboardView;
import edu.metrostate.brewcafe.view.ManagerLoginView;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Handles manager login, manager navigation, and manager-owned menu/inventory actions.
public class ManagerController {
    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;
    private final MenuItemFactory menuItemFactory = new MenuItemFactory();

    public ManagerController(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
    }

    public void showLoginScreen() {
        rootLayout.setCenter(new ManagerLoginView(rootLayout, applicationState).build());
    }

    public void showDashboardScreen() {
        rootLayout.setCenter(new ManagerDashboardView(rootLayout, applicationState).build());
    }

    public void returnToHome() {
        rootLayout.setCenter(RolePlaceholderFactory.createHomePlaceholder(rootLayout, applicationState));
    }

    public void handleManagerLogin(String username, String password, Label statusLabel) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            statusLabel.setText("Enter both username and password.");
            return;
        }

        boolean authenticated = applicationState.getAuthService()
                .authenticate(username.trim(), password, UserRole.MANAGER)
                .isPresent();

        if (!authenticated) {
            statusLabel.setText("Invalid manager credentials.");
            return;
        }

        showDashboardScreen();
    }

    public void addMenuItem(
            String itemType,
            String name,
            String basePriceValue,
            String variation,
            ObservableList<MenuItem> menuItems,
            Label statusLabel
    ) {
        if (itemType == null || itemType.isBlank() || name == null || name.isBlank() || basePriceValue == null || basePriceValue.isBlank()) {
            statusLabel.setText("Type, name, and base price are required.");
            return;
        }

        double basePrice;
        try {
            basePrice = Double.parseDouble(basePriceValue.trim());
        } catch (NumberFormatException exception) {
            statusLabel.setText("Base price must be a number.");
            return;
        }

        if (basePrice < 0) {
            statusLabel.setText("Base price must be 0 or higher.");
            return;
        }

        String generatedId = buildMenuItemId(itemType, name);
        MenuItem menuItem;

        if ("Beverage".equals(itemType)) {
            Beverage beverage = (Beverage) menuItemFactory.createBeverage(generatedId, name.trim(), basePrice);
            beverage.addSize(new SizeOption("Small", 0.0));
            beverage.addSize(new SizeOption("Medium", 0.75));
            beverage.addSize(new SizeOption("Large", 1.50));
            beverage.addCustomization(new Customization("Extra Shot", 0.75));
            beverage.addCustomization(new Customization("Oat Milk", 0.65));
            menuItem = beverage;
        } else {
            String pastryVariation = variation == null || variation.isBlank() ? "Standard" : variation.trim();
            menuItem = menuItemFactory.createPastry(generatedId, name.trim(), basePrice, pastryVariation);
        }

        applicationState.getMenuService().addMenuItem(menuItem);
        try {
            applicationState.saveMenuData();
        } catch (IOException exception) {
            statusLabel.setText("Menu updated, but saving to JSON failed.");
            refreshMenuItems(menuItems);
            return;
        }

        refreshMenuItems(menuItems);
        statusLabel.setText(itemType + " added and saved.");
    }

    public void removeMenuItem(MenuItem selectedMenuItem, ObservableList<MenuItem> menuItems, Label statusLabel) {
        if (selectedMenuItem == null) {
            statusLabel.setText("Select a menu item to remove.");
            return;
        }

        boolean removed = applicationState.getMenuService().removeMenuItem(selectedMenuItem.getId());
        if (!removed) {
            statusLabel.setText("Unable to remove the selected menu item.");
            return;
        }

        try {
            applicationState.saveMenuData();
        } catch (IOException exception) {
            statusLabel.setText("Menu updated, but saving to JSON failed.");
            refreshMenuItems(menuItems);
            return;
        }

        refreshMenuItems(menuItems);
        statusLabel.setText("Removed " + selectedMenuItem.getName() + " and saved the menu.");
    }

    public void updateMenuItem(
            MenuItem selectedMenuItem,
            String name,
            String basePriceValue,
            String variation,
            String sizesValue,
            String customizationsValue,
            String ingredientUsagesValue,
            ObservableList<MenuItem> menuItems,
            Label statusLabel
    ) {
        if (selectedMenuItem == null) {
            statusLabel.setText("Select a menu item to edit.");
            return;
        }

        if (name == null || name.isBlank() || basePriceValue == null || basePriceValue.isBlank()) {
            statusLabel.setText("Name and base price are required for edits.");
            return;
        }

        double basePrice;
        try {
            basePrice = Double.parseDouble(basePriceValue.trim());
        } catch (NumberFormatException exception) {
            statusLabel.setText("Base price must be a number.");
            return;
        }

        if (basePrice < 0) {
            statusLabel.setText("Base price must be 0 or higher.");
            return;
        }

        selectedMenuItem.setName(name.trim());
        selectedMenuItem.setBasePrice(basePrice);
        List<IngredientUsage> ingredientUsages;
        try {
            ingredientUsages = parseIngredientUsages(ingredientUsagesValue);
        } catch (IllegalArgumentException exception) {
            statusLabel.setText(exception.getMessage());
            return;
        }
        selectedMenuItem.setIngredientUsages(ingredientUsages);

        if (selectedMenuItem instanceof Beverage beverage) {
            try {
                beverage.setSizes(parseSizeOptions(sizesValue));
                beverage.setCustomizations(parseCustomizations(customizationsValue));
            } catch (IllegalArgumentException exception) {
                statusLabel.setText(exception.getMessage());
                return;
            }
        }

        if (selectedMenuItem instanceof Pastry pastry) {
            String pastryVariation = variation == null || variation.isBlank() ? "Standard" : variation.trim();
            pastry.setVariation(pastryVariation);
        }

        boolean updated = applicationState.getMenuService().updateMenuItem(selectedMenuItem);
        if (!updated) {
            statusLabel.setText("Unable to update the selected menu item.");
            return;
        }

        try {
            applicationState.saveMenuData();
        } catch (IOException exception) {
            statusLabel.setText("Menu updated, but saving to JSON failed.");
            refreshMenuItems(menuItems);
            return;
        }

        refreshMenuItems(menuItems);
        statusLabel.setText("Updated " + selectedMenuItem.getName() + " and saved the menu.");
    }

    public void restockIngredient(
            Ingredient selectedIngredient,
            String quantityValue,
            ObservableList<Ingredient> ingredients,
            Label statusLabel
    ) {
        if (selectedIngredient == null) {
            statusLabel.setText("Select an ingredient to restock.");
            return;
        }

        if (quantityValue == null || quantityValue.isBlank()) {
            statusLabel.setText("Enter a restock quantity.");
            return;
        }

        double quantityToAdd;
        try {
            quantityToAdd = Double.parseDouble(quantityValue.trim());
        } catch (NumberFormatException exception) {
            statusLabel.setText("Restock quantity must be a number.");
            return;
        }

        boolean restocked = applicationState.getInventoryService()
                .restockIngredient(selectedIngredient.getId(), quantityToAdd);

        if (!restocked) {
            statusLabel.setText("Restock amount must be greater than 0.");
            return;
        }

        try {
            applicationState.saveInventoryData();
        } catch (IOException exception) {
            statusLabel.setText("Inventory updated, but saving to JSON failed.");
            refreshIngredients(ingredients);
            return;
        }

        refreshIngredients(ingredients);
        statusLabel.setText("Restocked " + selectedIngredient.getName() + " and saved inventory.");
    }

    public void setIngredientQuantity(
            Ingredient selectedIngredient,
            String quantityValue,
            ObservableList<Ingredient> ingredients,
            Label statusLabel
    ) {
        if (selectedIngredient == null) {
            statusLabel.setText("Select an ingredient to edit.");
            return;
        }

        if (quantityValue == null || quantityValue.isBlank()) {
            statusLabel.setText("Enter the new inventory quantity.");
            return;
        }

        double newQuantity;
        try {
            newQuantity = Double.parseDouble(quantityValue.trim());
        } catch (NumberFormatException exception) {
            statusLabel.setText("Inventory quantity must be a number.");
            return;
        }

        boolean updated = applicationState.getInventoryService()
                .setIngredientQuantity(selectedIngredient.getId(), newQuantity);

        if (!updated) {
            statusLabel.setText("Inventory quantity must be 0 or higher.");
            return;
        }

        try {
            applicationState.saveInventoryData();
        } catch (IOException exception) {
            statusLabel.setText("Inventory updated, but saving to JSON failed.");
            refreshIngredients(ingredients);
            return;
        }

        refreshIngredients(ingredients);
        statusLabel.setText("Set " + selectedIngredient.getName() + " quantity to " + String.format("%.2f", newQuantity) + ".");
    }

    public void refreshMenuItems(ObservableList<MenuItem> menuItems) {
        menuItems.setAll(applicationState.getMenuService().getAllMenuItems());
    }

    public void refreshIngredients(ObservableList<Ingredient> ingredients) {
        ingredients.setAll(applicationState.getInventoryService().getAllIngredients());
    }

    private String buildMenuItemId(String itemType, String name) {
        String prefix = "Beverage".equals(itemType) ? "bev" : "pas";
        String normalizedName = name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return prefix + "-" + normalizedName + "-" + System.currentTimeMillis();
    }

    private List<SizeOption> parseSizeOptions(String sizesValue) {
        List<SizeOption> sizes = new ArrayList<>();
        for (String entry : splitCsvEntries(sizesValue)) {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2 || parts[0].isBlank()) {
                throw new IllegalArgumentException("Use size format like Small:0, Medium:0.75.");
            }
            sizes.add(new SizeOption(parts[0].trim(), parseNonNegativeDouble(parts[1], "Size price adjustment")));
        }

        if (sizes.isEmpty()) {
            throw new IllegalArgumentException("Beverages need at least one size.");
        }

        return sizes;
    }

    private List<Customization> parseCustomizations(String customizationsValue) {
        List<Customization> customizations = new ArrayList<>();
        for (String entry : splitCsvEntries(customizationsValue)) {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2 || parts[0].isBlank()) {
                throw new IllegalArgumentException("Use customization format like Extra Shot:0.75, Oat Milk:0.65.");
            }
            customizations.add(new Customization(parts[0].trim(), parseNonNegativeDouble(parts[1], "Customization price")));
        }
        return customizations;
    }

    private List<IngredientUsage> parseIngredientUsages(String ingredientUsagesValue) {
        List<IngredientUsage> usages = new ArrayList<>();
        for (String entry : splitCsvEntries(ingredientUsagesValue)) {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2 || parts[0].isBlank()) {
                throw new IllegalArgumentException("Use ingredient format like ing-milk:180, ing-coffee-beans:18.");
            }

            String ingredientId = parts[0].trim();
            if (applicationState.getInventoryService().getIngredientById(ingredientId).isEmpty()) {
                throw new IllegalArgumentException("Unknown ingredient id: " + ingredientId);
            }

            usages.add(new IngredientUsage(
                    ingredientId,
                    parseNonNegativeDouble(parts[1], "Ingredient quantity")
            ));
        }
        return usages;
    }

    private List<String> splitCsvEntries(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        List<String> entries = new ArrayList<>();
        for (String entry : value.split(",")) {
            if (!entry.isBlank()) {
                entries.add(entry.trim());
            }
        }
        return entries;
    }

    private double parseNonNegativeDouble(String value, String fieldName) {
        try {
            double parsed = Double.parseDouble(value.trim());
            if (parsed < 0) {
                throw new IllegalArgumentException(fieldName + " must be 0 or higher.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }
}
