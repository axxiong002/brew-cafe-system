package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.User;
import edu.metrostate.brewcafe.model.Ingredient;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.persistence.JsonDataLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// Holds shared service instances so every screen talks to the same application state.
public class CafeApplicationState {
    private final AuthService authService;
    private final MenuService menuService;
    private final InventoryService inventoryService;
    private final OrderService orderService;

    public CafeApplicationState(
            AuthService authService,
            MenuService menuService,
            InventoryService inventoryService,
            OrderService orderService
    ) {
        this.authService = authService;
        this.menuService = menuService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
    }

    public static CafeApplicationState fromDefaultData() throws IOException {
        JsonDataLoader dataLoader = new JsonDataLoader();
        List<User> users = dataLoader.loadUsers(Path.of("data", "users.json"));
        List<MenuItem> menuItems = dataLoader.loadMenuItems(Path.of("data", "menu.json"));
        List<Ingredient> ingredients = dataLoader.loadIngredients(Path.of("data", "inventory.json"));

        return new CafeApplicationState(
                new AuthService(users),
                new MenuService(menuItems),
                new InventoryService(ingredients),
                new OrderService()
        );
    }

    public AuthService getAuthService() {
        return authService;
    }

    public MenuService getMenuService() {
        return menuService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public OrderService getOrderService() {
        return orderService;
    }
}
