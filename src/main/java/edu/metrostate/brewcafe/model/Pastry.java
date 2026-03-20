package edu.metrostate.brewcafe.model;

// Starter pastry model that keeps a simple variation value like Butter or Blueberry.
public class Pastry extends MenuItem {
    private String variation;

    public Pastry(String id, String name, double basePrice, String variation) {
        super(id, name, basePrice);
        this.variation = variation;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }
}
