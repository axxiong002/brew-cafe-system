package edu.metrostate.brewcafe.controller;

import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.Ingredient;
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
}
