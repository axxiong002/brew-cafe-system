package edu.metrostate.brewcafe.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Shared base class for menu items like beverages and pastries.
public abstract class MenuItem {
    private final String id;
    private String name;
    private double basePrice;
    private final List<IngredientUsage> ingredientUsages = new ArrayList<>();

    protected MenuItem(String id, String name, double basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public List<IngredientUsage> getIngredientUsages() {
        return Collections.unmodifiableList(ingredientUsages);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setIngredientUsages(List<IngredientUsage> usages) {
        ingredientUsages.clear();
        ingredientUsages.addAll(usages);
    }

    public void addIngredientUsage(IngredientUsage usage) {
        ingredientUsages.add(usage);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MenuItem other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", basePrice=" + basePrice +
                '}';
    }
}
