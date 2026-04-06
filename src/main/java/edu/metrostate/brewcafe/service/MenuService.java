package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Central manager-owned service for menu listing and CRUD operations.
public class MenuService extends AbstractCafeSubject {
    private final List<MenuItem> menuItems;

    public MenuService(List<MenuItem> menuItems) {
        this.menuItems = new ArrayList<>(menuItems);
    }

    public List<MenuItem> getAllMenuItems() {
        return Collections.unmodifiableList(menuItems);
    }

    public List<MenuItem> getAvailableMenuItems(InventoryService inventoryService) {
        return menuItems.stream()
                .filter(inventoryService::isAvailable)
                .toList();
    }

    public Optional<MenuItem> getMenuItemById(String menuItemId) {
        return menuItems.stream()
                .filter(menuItem -> menuItem.getId().equals(menuItemId))
                .findFirst();
    }

    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        notifyObservers("menu-item-added");
    }

    public boolean removeMenuItem(String menuItemId) {
        boolean removed = menuItems.removeIf(menuItem -> menuItem.getId().equals(menuItemId));
        if (removed) {
            notifyObservers("menu-item-removed");
        }
        return removed;
    }

    public boolean updateMenuItem(MenuItem updatedMenuItem) {
        Optional<MenuItem> existingItem = getMenuItemById(updatedMenuItem.getId());
        if (existingItem.isEmpty()) {
            return false;
        }

        int index = menuItems.indexOf(existingItem.get());
        menuItems.set(index, updatedMenuItem);
        notifyObservers("menu-item-updated");
        return true;
    }
}
