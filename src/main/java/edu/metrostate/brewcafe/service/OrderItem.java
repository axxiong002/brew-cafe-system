package edu.metrostate.brewcafe.model;

import java.util.ArrayList;
import java.util.List;
/**
 * 	Stores the selected menu item with quantity and the customizations of order
 */
public class OrderItem {
	private final MenuItem menuItem;
    private int quantity;
    private SizeOption selectedSize;
    private List<Customization> selectedCustomizations;

    public OrderItem(MenuItem menuItem, int quantity,
    		SizeOption selectedSize, List<Customization> selectedCustomization) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
        this.selectedCustomizations = new ArrayList<>();
        
        if (selectedCustomization != null)
        	this.selectedCustomizations.addAll(selectedCustomization);
    }
    
    public OrderItem(MenuItem menuItem, int quantity) {
    	this(menuItem, quantity, null, new ArrayList<>());
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
    	return selectedCustomizations;
    }
    
    public void setSelectedCustomizations(List<Customization> newCustomizations) {
    	selectedCustomizations.clear();
    	if (newCustomizations != null)
    		selectedCustomizations.addAll(newCustomizations);
    }
}
