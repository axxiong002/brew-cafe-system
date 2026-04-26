package edu.metrostate.brewcafe.model;

import java.util.ArrayList;
import java.util.List;

// Starter beverage model with room for sizes and add-on customizations.
public class Beverage extends MenuItem {
    private final List<SizeOption> sizes = new ArrayList<>();
    private final List<Customization> customizations = new ArrayList<>();

    public Beverage(String id, String name, double basePrice) {
        super(id, name, basePrice);
    }

    public List<SizeOption> getSizes() {
        return sizes;
    }

    public List<Customization> getCustomizations() {
        return customizations;
    }

    public void addSize(SizeOption sizeOption) {
        sizes.add(sizeOption);
    }

    public void setSizes(List<SizeOption> sizeOptions) {
        sizes.clear();
        sizes.addAll(sizeOptions);
    }

    public void addCustomization(Customization customization) {
        customizations.add(customization);
    }

    public void setCustomizations(List<Customization> customizationOptions) {
        customizations.clear();
        customizations.addAll(customizationOptions);
    }
}
