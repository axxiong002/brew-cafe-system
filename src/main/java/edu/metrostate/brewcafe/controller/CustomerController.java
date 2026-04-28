package edu.metrostate.brewcafe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.SizeOption;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.service.CustomerOrderService;
import edu.metrostate.brewcafe.service.OrderPricingService;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

// Handle customer screen actions and also connects UI to applicationState and customerOrderService
public class CustomerController {
    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;
    private final CustomerOrderService customerOrderService;
    private final OrderPricingService orderPricingService;
    
    public CustomerController(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
        this.customerOrderService = new CustomerOrderService(applicationState);
        this.orderPricingService = new OrderPricingService();
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

        Optional<Order> currentOrderOptional = customerOrderService.getCurrentOrder();

        if (currentOrderOptional.isEmpty()) {
            try {
                customerOrderService.startNewOrder(customerName.trim());
                currentOrderOptional = customerOrderService.getCurrentOrder();
            } catch (IllegalArgumentException ex) {
                statusLabel.setText(ex.getMessage());
                return;
            }
        }

        Order currentOrder = currentOrderOptional.orElseThrow();

        if (!currentOrder.getCustomerName().equals(customerName.trim())) {
            statusLabel.setText("Clear or place the current order before changing the customer name.");
            return;
        }

        if (selectedItem instanceof Beverage && selectedSize == null) {
            statusLabel.setText("Select a size for the beverage.");
            return;
        }

        try {
            OrderItem addedItem = customerOrderService.addItemToCurrentOrder(selectedItem, quantity, selectedSize, new ArrayList<>(selectedCustomizations));

            Order updateOrder = customerOrderService.getCurrentOrder().orElseThrow();
            if (!applicationState.getInventoryService().canFulfillOrder(updateOrder)) {
                customerOrderService.removeItemFromCurrentOrder(addedItem);
                statusLabel.setText("That item cannot be added because ingredients are insufficient");
                refreshCurrentOrderItems(currentOrderItems);
                refreshTotal(totalLabel);
                return;
            }

            refreshCurrentOrderItems(currentOrderItems);
            refreshTotal(totalLabel);
            statusLabel.setText("Item added to the order.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    public void clearCurrentOrder(ObservableList<OrderItem> currentOrderItems, Label totalLabel, Label statusLabel) {
        Optional<Order> currentOrderOptional = customerOrderService.getCurrentOrder();

        if (currentOrderOptional.isEmpty() || currentOrderOptional.get().getItems().isEmpty()) {
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
    								TextField customerNameField, ListView<MenuItem> menuListView,
    								ComboBox<SizeOption> sizeComboBox, Spinner<Integer> quantitySpinner) {
        try {
            Order placedOrder = customerOrderService.placeCurrentOrder();
            currentOrderItems.clear();
            totalLabel.setText("Total: $0.00");
            resetInputs(customerNameField, menuListView, sizeComboBox, quantitySpinner);
            statusLabel.setText("Order placed: " + placedOrder.getId());
        } catch (IllegalStateException ex) {
            statusLabel.setText(ex.getMessage());
        } catch (IOException ex) {
            currentOrderItems.clear();
            totalLabel.setText("Total: $0.00");
            resetInputs(customerNameField, menuListView, sizeComboBox, quantitySpinner);
            statusLabel.setText("Order placed, but saving failed.");
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
        text.append(" - $").append(String.format("%.2f", orderPricingService.calculateLineTotal(item)));
        return text.toString();
    }

    private void refreshCurrentOrderItems(ObservableList<OrderItem> currentOrderItems) {
        Optional<Order> currentOrderOptional = customerOrderService.getCurrentOrder();
        if (currentOrderOptional.isEmpty()) {
            currentOrderItems.clear();
            return;
        }
        currentOrderItems.setAll(currentOrderOptional.get().getItems());
    }

    private void refreshTotal(Label totalLabel) {
        Optional<Order> currentOrderOptional = customerOrderService.getCurrentOrder();
        if (currentOrderOptional.isEmpty()) {
            totalLabel.setText("Total: $0.00");
            return;
        }

        double total = orderPricingService.calculateOrderTotal(currentOrderOptional.get());
        totalLabel.setText("Total: $" + String.format("%.2f", total));
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
