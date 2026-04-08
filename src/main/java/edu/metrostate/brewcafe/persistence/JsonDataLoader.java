package edu.metrostate.brewcafe.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.Ingredient;
import edu.metrostate.brewcafe.model.User;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Pastry;
import edu.metrostate.brewcafe.model.SizeOption;
import edu.metrostate.brewcafe.service.MenuItemFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        MenuData menuData = objectMapper.readValue(path.toFile(), MenuData.class);
        List<MenuItem> menuItems = new ArrayList<>();

        for (BeverageData beverageData : menuData.beverages()) {
            Beverage beverage = (Beverage) menuItemFactory.createBeverage(
                    beverageData.id(),
                    beverageData.name(),
                    beverageData.basePrice()
            );

            for (String sizeName : beverageData.sizes()) {
                beverage.addSize(new SizeOption(sizeName, 0.0));
            }

            for (String customizationName : beverageData.customizations()) {
                beverage.addCustomization(new Customization(customizationName, 0.0));
            }

            menuItems.add(beverage);
        }

        for (PastryData pastryData : menuData.pastries()) {
            menuItems.add(menuItemFactory.createPastry(
                    pastryData.id(),
                    pastryData.name(),
                    pastryData.basePrice(),
                    pastryData.variation()
            ));
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
                List<String> sizes = beverage.getSizes().stream()
                        .map(SizeOption::name)
                        .toList();

                List<String> customizations = beverage.getCustomizations().stream()
                        .map(Customization::name)
                        .toList();

                beverages.add(new BeverageData(
                        beverage.getId(),
                        beverage.getName(),
                        beverage.getBasePrice(),
                        sizes,
                        customizations
                ));
            } else if (menuItem instanceof Pastry pastry) {
                pastries.add(new PastryData(
                        pastry.getId(),
                        pastry.getName(),
                        pastry.getVariation(),
                        pastry.getBasePrice()
                ));
            }
        }

        objectMapper.writeValue(path.toFile(), new MenuData(beverages, pastries));
    }

    public void saveIngredients(Path path, List<Ingredient> ingredients) throws IOException {
        objectMapper.writeValue(path.toFile(), new IngredientsData(ingredients));
    }

    private record UsersData(List<User> users) {
    }

    private record MenuData(List<BeverageData> beverages, List<PastryData> pastries) {
    }

    private record BeverageData(
            String id,
            String name,
            double basePrice,
            List<String> sizes,
            List<String> customizations
    ) {
    }

    private record PastryData(String id, String name, String variation, double basePrice) {
    }

    private record IngredientsData(List<Ingredient> ingredients) {
    }
}
