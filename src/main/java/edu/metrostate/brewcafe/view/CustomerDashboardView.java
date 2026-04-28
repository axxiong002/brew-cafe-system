package edu.metrostate.brewcafe.view;

import java.util.ArrayList;
import java.util.List;

import edu.metrostate.brewcafe.controller.CustomerController;
import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Customization;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.OrderItem;
import edu.metrostate.brewcafe.model.Pastry;
import edu.metrostate.brewcafe.model.SizeOption;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.service.CafeObserver;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomerDashboardView implements CafeObserver{

    private final CafeApplicationState applicationState;
    private final CustomerController customerController;
    private final ObservableList<OrderItem> currentOrderItems = FXCollections.observableArrayList();

    private final ListView<MenuItem> menuListView = new ListView<>();
    private final ComboBox<SizeOption> sizeComboBox = new ComboBox<>();
    private final FlowPane customizationPane = new FlowPane();
    private final Spinner<Integer> quantitySpinner = new Spinner<>(1, 20, 1);
    private final TableView<OrderItem> orderTableView = new TableView<>();

    private final Label selectedItemLabel = new Label("Choose an item to begin.");
    private final Label statusLabel = new Label("Enter your name, select items, and place an order.");
    private final Label totalLabel = new Label("$0.00");
    private final Label previewLabel = new Label("Order preview will appear here.");
    private final TextField customerNameField = new TextField();

    public CustomerDashboardView(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.applicationState = applicationState;
        this.customerController = new CustomerController(rootLayout, applicationState);
        applicationState.getInventoryService().addObserver(this);
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #fbf8f4;");

        root.setTop(buildHeader());
        root.setLeft(buildMenuPanel());
        root.setCenter(buildOptionsPanel());
        root.setRight(buildOrderPanel());
        root.setBottom(buildStatusArea());

        menuListView.setItems(FXCollections.observableArrayList(applicationState.getMenuService().getAllMenuItems()));
        orderTableView.setItems(currentOrderItems);

        if (!menuListView.getItems().isEmpty()) {
            menuListView.getSelectionModel().selectFirst();
            refreshSelectionDetails();
        }

        return root;
    }

    private VBox buildHeader() {
        Label titleLabel = new Label("Customer Ordering");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Browse the menu, customize beverages, and submit your order.");
        subtitleLabel.setStyle("-fx-font-size: 13px;");

        customerNameField.setPromptText("Enter customer name");
        customerNameField.setPrefWidth(260);

        Button backButton = new Button("Back to Home");
        backButton.setOnAction(event -> { applicationState.getInventoryService().removeObserver(this);
        									customerController.returnToHome();
        									});

        HBox controls = new HBox(12, new Label("Customer Name:"), customerNameField, backButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(8, titleLabel, subtitleLabel, controls);
        header.setPadding(new Insets(0, 0, 12, 0));
        return header;
    }

    private VBox buildMenuPanel() {
        Label menuLabel = new Label("Menu");
        menuLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        menuListView.setPrefWidth(280);
        menuListView.setPrefHeight(460);
        menuListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(buildMenuLabel(item) + " — " + formatMoney(item.getBasePrice()));
                }
            }
        });

        menuListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            refreshSelectionDetails();
        });

        VBox box = new VBox(10, menuLabel, menuListView);
        box.setPadding(new Insets(0, 12, 0, 0));
        return box;
    }

    private VBox buildOptionsPanel() {
        Label optionsLabel = new Label("Customize");
        optionsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        selectedItemLabel.setWrapText(true);
        selectedItemLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label sizeLabel = new Label("Size");
        sizeComboBox.setPrefWidth(220);
        sizeComboBox.setDisable(true);
        sizeComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(SizeOption item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatSizeOption(item));
                }
            }
        });
        sizeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SizeOption item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatSizeOption(item));
                }
            }
        });
        sizeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> updatePreview());

        Label customizationLabel = new Label("Customizations");
        customizationPane.setHgap(8);
        customizationPane.setVgap(8);
        customizationPane.setPrefWrapLength(280);
        customizationPane.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #d8cab8; -fx-border-radius: 6;");

        Label quantityLabel = new Label("Quantity");
        quantitySpinner.setEditable(true);
        quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> updatePreview());

        Button addButton = new Button("Add to Order");
        addButton.setPrefWidth(160);
        addButton.setOnAction(event -> handleAddToOrder());

        VBox panel = new VBox(10, optionsLabel, selectedItemLabel, sizeLabel, sizeComboBox, customizationLabel,
                			customizationPane, quantityLabel, quantitySpinner, previewLabel, addButton)
        		;
        panel.setPadding(new Insets(0, 12, 0, 12));
        panel.setPrefWidth(330);

        return panel;
    }

    private VBox buildOrderPanel() {
        Label orderLabel = new Label("Current Order");
        orderLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableColumn<OrderItem, String> itemColumn = new TableColumn<>("Item");
        itemColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(buildOrderItemLabel(cellData.getValue())));
        itemColumn.setPrefWidth(180);

        TableColumn<OrderItem, String> quantityColumn = new TableColumn<>("Qty");
        quantityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
        quantityColumn.setPrefWidth(55);

        TableColumn<OrderItem, String> unitPriceColumn = new TableColumn<>("Unit");
        unitPriceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatMoney(calculateUnitPrice(cellData.getValue()))));
        unitPriceColumn.setPrefWidth(80);

        TableColumn<OrderItem, String> totalColumn = new TableColumn<>("Line Total");
        totalColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatMoney(calculateLineTotal(cellData.getValue()))));
        totalColumn.setPrefWidth(95);

        orderTableView.getColumns().setAll(itemColumn, quantityColumn, unitPriceColumn, totalColumn);
        orderTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        orderTableView.setPrefHeight(430);

        Label totalTextLabel = new Label("Order Total:");
        totalTextLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox totalRow = new HBox(10, totalTextLabel, totalLabel);
        totalRow.setAlignment(Pos.CENTER_RIGHT);

        Button clearButton = new Button("Clear Order");
        clearButton.setOnAction(event -> handleClearOrder());

        Button placeButton = new Button("Place Order");
        placeButton.setOnAction(event -> handlePlaceOrder());

        HBox buttons = new HBox(10, clearButton, placeButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox panel = new VBox(10, orderLabel, orderTableView, totalRow, buttons);
        panel.setPrefWidth(420);
        return panel;
    }

    private VBox buildStatusArea() {
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-text-fill: #5a3d2b; -fx-font-weight: bold;");
        previewLabel.setWrapText(true);
        previewLabel.setStyle("-fx-text-fill: #6d5c4d;");

        VBox box = new VBox(6, statusLabel);
        box.setPadding(new Insets(12, 0, 0, 0));
        return box;
    }

    private void refreshSelectionDetails() {
        MenuItem selected = menuListView.getSelectionModel().getSelectedItem();

        customizationPane.getChildren().clear();
        sizeComboBox.getItems().clear();
        sizeComboBox.getSelectionModel().clearSelection();

        if (selected == null) {
            selectedItemLabel.setText("Choose an item to begin.");
            previewLabel.setText("Order preview will appear here.");
            sizeComboBox.setDisable(true);
            return;
        }

        selectedItemLabel.setText(buildMenuLabel(selected) + " | Base Price " + formatMoney(selected.getBasePrice()));

        if (selected instanceof Beverage beverage) {
            sizeComboBox.setDisable(false);
            sizeComboBox.getItems().setAll(beverage.getSizes());
            if (!beverage.getSizes().isEmpty()) {
                sizeComboBox.getSelectionModel().selectFirst();
            }

            if (beverage.getCustomizations().isEmpty()) {
                customizationPane.getChildren().add(new Label("No customizations for this item."));
            } else {
                for (Customization customization : beverage.getCustomizations()) {
                	String extraCost;
                	if (customization.extraCost() == 0.0) {
                		extraCost = "";
                	} else {
                		extraCost = "(" + formatMoney(customization.extraCost()) +")";
                	}
                    CheckBox checkBox = new CheckBox(customization.name() + extraCost);
                    checkBox.setUserData(customization);
                    checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> updatePreview());
                    customizationPane.getChildren().add(checkBox);
                }
            }
        } else {
            sizeComboBox.setDisable(true);
            customizationPane.getChildren().add(new Label("No customizations for this item."));
        }

        quantitySpinner.getValueFactory().setValue(1);
        updatePreview();
    }

    private void updatePreview() {
        MenuItem selected = menuListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            previewLabel.setText("Order preview will appear here.");
            return;
        }

        double unitPrice = selected.getBasePrice();

        if (sizeComboBox.getValue() != null) {
            unitPrice += sizeComboBox.getValue().priceAdjustment();
        }

        for (Customization customization : getSelectedCustomizations()) {
            unitPrice += customization.extraCost();
        }

        int quantity = quantitySpinner.getValue();
        double lineTotal = unitPrice * quantity;

        previewLabel.setText("Preview: " + buildMenuLabel(selected)
                + " x" + quantity
                + " | Estimated line total " + formatMoney(lineTotal));
    }

    private List<Customization> getSelectedCustomizations() {
        List<Customization> selectedCustomizations = new ArrayList<>();
        customizationPane.getChildren().forEach(node -> {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()
                    && checkBox.getUserData() instanceof Customization customization) {
            	
                selectedCustomizations.add(customization);
            }
        });
        return selectedCustomizations;
    }

    private void handleAddToOrder() {
        customerController.addItemToCurrentOrder(customerNameField.getText(), menuListView.getSelectionModel().getSelectedItem(),
        										sizeComboBox.getValue(), getSelectedCustomizations(), quantitySpinner.getValue(),
        										currentOrderItems, totalLabel, statusLabel);
        orderTableView.refresh();
        updatePreview();
    }

    private void handleClearOrder() {
        customerController.clearCurrentOrder(currentOrderItems, totalLabel, statusLabel);
        orderTableView.refresh();
        updatePreview();
    }

    private void handlePlaceOrder() {
        customerController.placeCurrentOrder(currentOrderItems, totalLabel, statusLabel, customerNameField,
                							menuListView, sizeComboBox, quantitySpinner);

        if (customerNameField.getText().isEmpty() && menuListView.getSelectionModel().getSelectedItem() == null) {
            customizationPane.getChildren().clear();
            selectedItemLabel.setText("Choose an item to begin.");
            previewLabel.setText("Order preview will appear here.");
        }

        orderTableView.refresh();
        updatePreview();
    }

    private String buildMenuLabel(MenuItem item) {
        String text = item.getName();
        if (item instanceof Pastry pastry) {
            text += " (" + pastry.getVariation() + ")";
        }
        return text;
    }

    private String buildOrderItemLabel(OrderItem item) {
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

        return text.toString();
    }

    private String formatSizeOption(SizeOption sizeOption) {
        if (sizeOption == null) {
            return "";
        }

        if (sizeOption.priceAdjustment() == 0.0) {
            return sizeOption.name();
        }

        return sizeOption.name() + " (" + (sizeOption.priceAdjustment() > 0 ? "+" : "") 
        		+ formatMoney(sizeOption.priceAdjustment()) + ")";
    }

    private double calculateUnitPrice(OrderItem item) {
        double unitPrice = item.getMenuItem().getBasePrice();

        if (item.getSelectedSize() != null) {
            unitPrice += item.getSelectedSize().priceAdjustment();
        }

        for (Customization customization : item.getSelectedCustomizations()) {
            unitPrice += customization.extraCost();
        }

        return unitPrice;
    }

    private double calculateLineTotal(OrderItem item) {
        return calculateUnitPrice(item) * item.getQuantity();
    }

    private String formatMoney(double value) {
        return String.format("$%.2f", value);
    }

	@Override
	public void onCafeStateChanged(String eventName) {
		menuListView.refresh();
		orderTableView.refresh();
		updatePreview();
		
		if("inventory-restocked".equals(eventName) || "inventory-quantity-updated".equals(eventName)
				|| "inventory-consumed".equals(eventName)) {
			
			statusLabel.setText("Inventory updated.");
		}
	}
}
