package edu.metrostate.brewcafe.controller;

import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.SizeOption;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.service.CustomerOrderService;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

// Handle customer screen actions and also connects UI to applicationState and customerOrderService
public class CustomerController {
    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;
    private final CustomerOrderService customerOrderService;

    public CustomerController(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;

        int startingOrderNumber = applicationState.getOrderService().getPendingOrders().size()
                        			+ applicationState.getOrderService().getFulfilledOrders().size()
                        			+ 1;

        this.customerOrderService = new CustomerOrderService(applicationState.getOrderService(),
        														applicationState.getInventoryService(),
        														startingOrderNumber);
    }

    public void returnToHome() {
        rootLayout.setCenter(RolePlaceholderFactory.createHomePlaceholder(rootLayout, applicationState));
    }

    public void handleMenuSelection(MenuItem selectedItem, ComboBox<SizeOption> sizeComboBox,
            						ListView<Customization> customizationListView, Label statusLabel) {

    	sizeComboBox.getItems().clear();
        sizeComboBox.getSelectionModel().clearSelection();

        customizationListView.getItems().clear();
        customizationListView.getSelectionModel().clearSelection();

        if (selectedItem instanceof Beverage beverage) {
            sizeComboBox.setDisable(false);
            customizationListView.setDisable(false);
            sizeComboBox.getItems().setAll(beverage.getSizes());
            customizationListView.getItems().setAll(beverage.getCustomizations());
            statusLabel.setText("Choose a size and any customizations.");
        } else {
            sizeComboBox.setDisable(true);
            customizationListView.setDisable(true);
            statusLabel.setText(selectedItem == null
                    ? "Enter your name and choose an item."
                    : "Pastry selected. No size or drink customizations needed.");
        }
    }

    public void addItemToCurrentOrder(String customerName, MenuItem selectedItem, SizeOption selectedSize,
            							Collection<Customization> selectedCustomizations, int quantity,
            							ObservableList<OrderItem> currentOrderItems, Label totalLabel,
            							Label statusLabel) {
        if (customerName == null || customerName.isBlank()) {
            statusLabel.setText("Enter a customer name first.");
            return;
        }

        if (selectedItem == null) {
            statusLabel.setText("Select an item first.");
            return;
        }

        if (quantity <= 0) {
            statusLabel.setText("Quantity must be at least 1.");
            return;
        }

        Order currentOrder = customerOrderService.getCurrentOrder();

        if (currentOrder == null) {
            try {
                customerOrderService.startNewOrder(customerName.trim());
                currentOrder = customerOrderService.getCurrentOrder();
            } catch (IllegalArgumentException ex) {
                statusLabel.setText(ex.getMessage());
                return;
            }
        }

        if (!currentOrder.getCustomerName().equals(customerName.trim())) {
            statusLabel.setText("Clear or place the current order before changing the customer name.");
            return;
        }

        if (selectedItem instanceof Beverage && selectedSize == null) {
            statusLabel.setText("Select a size for the beverage.");
            return;
        }

        OrderItem orderItem = new OrderItem(selectedItem, quantity, selectedSize, new ArrayList<>(selectedCustomizations));

        try {
            customerOrderService.addItemToCurrentOrder(orderItem);
            refreshCurrentOrderItems(currentOrderItems);
            refreshTotal(totalLabel);
            statusLabel.setText("Item added to the order.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    public void clearCurrentOrder(ObservableList<OrderItem> currentOrderItems, Label totalLabel, Label statusLabel) {
        Order currentOrder = customerOrderService.getCurrentOrder();

        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            currentOrderItems.clear();
            totalLabel.setText("Total: $0.00");
            statusLabel.setText("No active order to clear.");
            return;
        }

        try {
            customerOrderService.clearCurrentOrder();
            currentOrderItems.clear();
            totalLabel.setText("Total: $0.00");
            statusLabel.setText("Order cleared.");
        } catch (IllegalStateException ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    public void placeCurrentOrder(ObservableList<OrderItem> currentOrderItems, Label totalLabel, Label statusLabel,
    								TextField customerNameField,
    								ListView<MenuItem> menuListView,
    								ComboBox<SizeOption> sizeComboBox,
    								Spinner<Integer> quantitySpinner) {
        try {
            Order placedOrder = customerOrderService.placeCurrentOrder();

            try {
                applicationState.saveInventoryData();
                applicationState.saveOrderData();
            } catch (IOException exception) {
                String orderId = placedOrder.getId();
                currentOrderItems.clear();
                totalLabel.setText("Total: $0.00");
                resetInputs(customerNameField, menuListView, sizeComboBox, quantitySpinner);
                statusLabel.setText("Order placed: " + orderId + " (save failed)");
                return;
            }

            currentOrderItems.clear();
            totalLabel.setText("Total: $0.00");
            resetInputs(customerNameField, menuListView, sizeComboBox, quantitySpinner);
            statusLabel.setText("Order placed: " + placedOrder.getId());
        } catch (IllegalStateException ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    public String formatOrderItem(OrderItem item) {
        StringBuilder text = new StringBuilder();
        text.append(item.getMenuItem().getName());

        if (item.getSelectedSize() != null) {
            text.append(" (").append(item.getSelectedSize().name()).append(")");
        }

        if (!item.getSelectedCustomizations().isEmpty()) {
            text.append(" [");
            for (int i = 0; i < item.getSelectedCustomizations().size(); i++) {
                text.append(item.getSelectedCustomizations().get(i).name());
                if (i < item.getSelectedCustomizations().size() - 1) {
                    text.append(", ");
                }
            }
            text.append("]");
        }

        text.append(" x").append(item.getQuantity());
        text.append(" - $").append(String.format("%.2f", calculateLineTotal(item)));
        return text.toString();
    }

    private void refreshCurrentOrderItems(ObservableList<OrderItem> currentOrderItems) {
        Order currentOrder = customerOrderService.getCurrentOrder();
        if (currentOrder == null) {
            currentOrderItems.clear();
            return;
        }
        currentOrderItems.setAll(currentOrder.getItems());
    }

    private void refreshTotal(Label totalLabel) {
        Order currentOrder = customerOrderService.getCurrentOrder();
        if (currentOrder == null) {
            totalLabel.setText("Total: $0.00");
            return;
        }

        double total = 0.0;
        for (OrderItem item : currentOrder.getItems()) {
            total += calculateLineTotal(item);
        }
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private double calculateLineTotal(OrderItem item) {
        double unitPrice = item.getMenuItem().getBasePrice();

        if (item.getSelectedSize() != null) {
            unitPrice += item.getSelectedSize().priceAdjustment();
        }

        for (Customization customization : item.getSelectedCustomizations()) {
            unitPrice += customization.extraCost();
        }

        return unitPrice * item.getQuantity();
    }

    private void resetInputs(TextField customerNameField, ListView<MenuItem> menuListView,
            				ComboBox<SizeOption> sizeComboBox, Spinner<Integer> quantitySpinner) {
        customerNameField.clear();

        menuListView.getSelectionModel().clearSelection();

        sizeComboBox.getItems().clear();
        sizeComboBox.getSelectionModel().clearSelection();
        sizeComboBox.setDisable(true);

        quantitySpinner.getValueFactory().setValue(1);
    }
}
