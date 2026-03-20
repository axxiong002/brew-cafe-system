package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Pastry;

// Starter factory for centralizing beverage and pastry creation logic.
public class MenuItemFactory {
    public MenuItem createBeverage(String id, String name, double basePrice) {
        // Starter factory methods make it easier to evolve this into the required Factory Method pattern.
        return new Beverage(id, name, basePrice);
    }

    public MenuItem createPastry(String id, String name, double basePrice, String variation) {
        return new Pastry(id, name, basePrice, variation);
    }
}
