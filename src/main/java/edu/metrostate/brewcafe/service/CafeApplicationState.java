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
    private final JsonDataLoader dataLoader;
    private final Path menuDataPath;
    private final Path inventoryDataPath;
    private final AuthService authService;
    private final MenuService menuService;
    private final InventoryService inventoryService;
    private final OrderService orderService;

    public CafeApplicationState(
            JsonDataLoader dataLoader,
            Path menuDataPath,
            Path inventoryDataPath,
            AuthService authService,
            MenuService menuService,
            InventoryService inventoryService,
            OrderService orderService
    ) {
        this.dataLoader = dataLoader;
        this.menuDataPath = menuDataPath;
        this.inventoryDataPath = inventoryDataPath;
        this.authService = authService;
        this.menuService = menuService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
    }

    public static CafeApplicationState fromDefaultData() throws IOException {
        JsonDataLoader dataLoader = new JsonDataLoader();
        Path menuDataPath = Path.of("data", "menu.json");
        Path inventoryDataPath = Path.of("data", "inventory.json");
        List<User> users = dataLoader.loadUsers(Path.of("data", "users.json"));
        List<MenuItem> menuItems = dataLoader.loadMenuItems(menuDataPath);
        List<Ingredient> ingredients = dataLoader.loadIngredients(inventoryDataPath);

        return new CafeApplicationState(
                dataLoader,
                menuDataPath,
                inventoryDataPath,
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

    public void saveMenuData() throws IOException {
        dataLoader.saveMenuItems(menuDataPath, menuService.getAllMenuItems());
    }

    public void saveInventoryData() throws IOException {
        dataLoader.saveIngredients(inventoryDataPath, inventoryService.getAllIngredients());
    }
}
