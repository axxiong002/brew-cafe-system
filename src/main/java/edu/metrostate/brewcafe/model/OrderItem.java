package edu.metrostate.brewcafe.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderItem {
    private final MenuItem menuItem;
    private int quantity;
    private SizeOption selectedSize;
    private final List<Customization> selectedCustomizations = new ArrayList<>();

    public OrderItem(MenuItem menuItem, int quantity) {
        this(menuItem, quantity, null, List.of());
    }

    public OrderItem(MenuItem menuItem, int quantity, SizeOption selectedSize, List<Customization> selectedCustomizations) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
        setSelectedCustomizations(selectedCustomizations);
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public SizeOption getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(SizeOption selectedSize) {
        this.selectedSize = selectedSize;
    }

    public List<Customization> getSelectedCustomizations() {
        return Collections.unmodifiableList(selectedCustomizations);
    }

    public void setSelectedCustomizations(List<Customization> customizations) {
        selectedCustomizations.clear();
        if (customizations != null) {
            selectedCustomizations.addAll(customizations);
        }
    }
}