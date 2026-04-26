package edu.metrostate.brewcafe.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.Ingredient;
import edu.metrostate.brewcafe.model.IngredientUsage;
import edu.metrostate.brewcafe.model.User;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.OrderStatus;
import edu.metrostate.brewcafe.model.Pastry;
import edu.metrostate.brewcafe.model.SizeOption;
import edu.metrostate.brewcafe.service.MenuItemFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Simple file loader placeholder for moving JSON read logic out of the UI layer.
public class JsonDataLoader {
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final MenuItemFactory menuItemFactory = new MenuItemFactory();

    public String loadJson(Path path) throws IOException {
        // Keeps file loading isolated so JSON parsing can stay out of UI code later.
        return Files.readString(path);
    }

    public List<User> loadUsers(Path path) throws IOException {
        UsersData usersData = objectMapper.readValue(path.toFile(), UsersData.class);
        return usersData.users();
    }

    public List<MenuItem> loadMenuItems(Path path) throws IOException {
        JsonNode root = objectMapper.readTree(path.toFile());
        List<MenuItem> menuItems = new ArrayList<>();

        for (JsonNode beverageData : root.path("beverages")) {
            Beverage beverage = (Beverage) menuItemFactory.createBeverage(
                    beverageData.path("id").asText(),
                    beverageData.path("name").asText(),
                    beverageData.path("basePrice").asDouble()
            );

            beverage.setSizes(readSizeOptions(beverageData.path("sizes")));
            beverage.setCustomizations(readCustomizations(beverageData.path("customizations")));
            beverage.setIngredientUsages(readIngredientUsages(beverageData.path("ingredientUsages")));

            menuItems.add(beverage);
        }

        for (JsonNode pastryData : root.path("pastries")) {
            Pastry pastry = (Pastry) menuItemFactory.createPastry(
                    pastryData.path("id").asText(),
                    pastryData.path("name").asText(),
                    pastryData.path("basePrice").asDouble(),
                    pastryData.path("variation").asText("Standard")
            );
            pastry.setIngredientUsages(readIngredientUsages(pastryData.path("ingredientUsages")));
            menuItems.add(pastry);
        }

        return menuItems;
    }

    public List<Ingredient> loadIngredients(Path path) throws IOException {
        IngredientsData ingredientsData = objectMapper.readValue(path.toFile(), IngredientsData.class);
        return ingredientsData.ingredients();
    }

    public void saveMenuItems(Path path, List<MenuItem> menuItems) throws IOException {
        List<BeverageData> beverages = new ArrayList<>();
        List<PastryData> pastries = new ArrayList<>();

        for (MenuItem menuItem : menuItems) {
            if (menuItem instanceof Beverage beverage) {
                beverages.add(new BeverageData(
                        beverage.getId(),
                        beverage.getName(),
                        beverage.getBasePrice(),
                        beverage.getSizes(),
                        beverage.getCustomizations(),
                        beverage.getIngredientUsages()
                ));
            } else if (menuItem instanceof Pastry pastry) {
                pastries.add(new PastryData(
                        pastry.getId(),
                        pastry.getName(),
                        pastry.getVariation(),
                        pastry.getBasePrice(),
                        pastry.getIngredientUsages()
                ));
            }
        }

        objectMapper.writeValue(path.toFile(), new MenuData(beverages, pastries));
    }

    public void saveIngredients(Path path, List<Ingredient> ingredients) throws IOException {
        objectMapper.writeValue(path.toFile(), new IngredientsData(ingredients));
    }

    public OrdersData loadOrders(Path path, List<MenuItem> menuItems) throws IOException {
        if (Files.notExists(path)) {
            return new OrdersData(List.of(), List.of());
        }

        OrdersDataRaw ordersData = objectMapper.readValue(path.toFile(), OrdersDataRaw.class);
        Map<String, MenuItem> menuById = menuItems.stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        return new OrdersData(
                readOrders(ordersData.pendingOrders(), menuById),
                readOrders(ordersData.fulfilledOrders(), menuById)
        );
    }

    public void saveOrders(Path path, List<Order> pendingOrders, List<Order> fulfilledOrders) throws IOException {
        objectMapper.writeValue(path.toFile(), new OrdersDataRaw(
                writeOrders(pendingOrders),
                writeOrders(fulfilledOrders)
        ));
    }

    private List<SizeOption> readSizeOptions(JsonNode sizeNodes) {
        List<SizeOption> sizes = new ArrayList<>();
        for (JsonNode sizeNode : sizeNodes) {
            if (sizeNode.isTextual()) {
                sizes.add(new SizeOption(sizeNode.asText(), 0.0));
            } else {
                sizes.add(new SizeOption(
                        sizeNode.path("name").asText(),
                        sizeNode.path("priceAdjustment").asDouble()
                ));
            }
        }
        return sizes;
    }

    private List<Customization> readCustomizations(JsonNode customizationNodes) {
        List<Customization> customizations = new ArrayList<>();
        for (JsonNode customizationNode : customizationNodes) {
            if (customizationNode.isTextual()) {
                customizations.add(new Customization(customizationNode.asText(), 0.0));
            } else {
                customizations.add(new Customization(
                        customizationNode.path("name").asText(),
                        customizationNode.path("extraCost").asDouble()
                ));
            }
        }
        return customizations;
    }

    private List<IngredientUsage> readIngredientUsages(JsonNode usageNodes) {
        List<IngredientUsage> usages = new ArrayList<>();
        for (JsonNode usageNode : usageNodes) {
            usages.add(new IngredientUsage(
                    usageNode.path("ingredientId").asText(),
                    usageNode.path("quantityRequired").asDouble()
            ));
        }
        return usages;
    }

    private List<Order> readOrders(List<OrderDataRaw> orderData, Map<String, MenuItem> menuById) {
        List<Order> orders = new ArrayList<>();
        if (orderData == null) {
            return orders;
        }

        for (OrderDataRaw rawOrder : orderData) {
            Order order = new Order(rawOrder.id(), rawOrder.customerName(), rawOrder.status());
            for (OrderItemDataRaw rawItem : rawOrder.items()) {
                MenuItem menuItem = menuById.get(rawItem.menuItemId());
                if (menuItem != null) {
                    order.addItem(new OrderItem(
                            menuItem,
                            rawItem.quantity(),
                            findSelectedSize(menuItem, rawItem.selectedSizeName()),
                            findSelectedCustomizations(menuItem, rawItem.selectedCustomizationNames())
                    ));
                }
            }
            orders.add(order);
        }

        return orders;
    }

    private List<OrderDataRaw> writeOrders(List<Order> orders) {
        List<OrderDataRaw> orderData = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItemDataRaw> items = order.getItems().stream()
                    .map(item -> new OrderItemDataRaw(
                            item.getMenuItem().getId(),
                            item.getQuantity(),
                            item.getSelectedSize() == null ? null : item.getSelectedSize().name(),
                            item.getSelectedCustomizations().stream()
                                    .map(Customization::name)
                                    .toList()
                    ))
                    .toList();
            orderData.add(new OrderDataRaw(order.getId(), order.getCustomerName(), order.getStatus(), items));
        }
        return orderData;
    }

    private SizeOption findSelectedSize(MenuItem menuItem, String selectedSizeName) {
        if (!(menuItem instanceof Beverage beverage) || selectedSizeName == null || selectedSizeName.isBlank()) {
            return null;
        }

        return beverage.getSizes().stream()
                .filter(size -> size.name().equals(selectedSizeName))
                .findFirst()
                .orElse(null);
    }

    private List<Customization> findSelectedCustomizations(MenuItem menuItem, List<String> customizationNames) {
        if (!(menuItem instanceof Beverage beverage) || customizationNames == null || customizationNames.isEmpty()) {
            return List.of();
        }

        return beverage.getCustomizations().stream()
                .filter(customization -> customizationNames.contains(customization.name()))
                .toList();
    }

    private record UsersData(List<User> users) {
    }

    private record MenuData(List<BeverageData> beverages, List<PastryData> pastries) {
    }

    private record BeverageData(
            String id,
            String name,
            double basePrice,
            List<SizeOption> sizes,
            List<Customization> customizations,
            List<IngredientUsage> ingredientUsages
    ) {
    }

    private record PastryData(
            String id,
            String name,
            String variation,
            double basePrice,
            List<IngredientUsage> ingredientUsages
    ) {
    }

    private record IngredientsData(List<Ingredient> ingredients) {
    }

    public record OrdersData(List<Order> pendingOrders, List<Order> fulfilledOrders) {
    }

    private record OrdersDataRaw(List<OrderDataRaw> pendingOrders, List<OrderDataRaw> fulfilledOrders) {
    }

    private record OrderDataRaw(String id, String customerName, OrderStatus status, List<OrderItemDataRaw> items) {
    }

    private record OrderItemDataRaw(
            String menuItemId,
            int quantity,
            String selectedSizeName,
            List<String> selectedCustomizationNames
    ) {
    }
}
