package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.controller.ManagerController;
import edu.metrostate.brewcafe.model.Beverage;
import edu.metrostate.brewcafe.model.Ingredient;
import edu.metrostate.brewcafe.model.MenuItem;
import edu.metrostate.brewcafe.model.Pastry;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

// Builds the first manager dashboard for menu and inventory operations.
public class ManagerDashboardView {
    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;

    public ManagerDashboardView(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
    }

    public Parent build() {
        ManagerController controller = new ManagerController(rootLayout, applicationState);
        ObservableList<MenuItem> menuItems = FXCollections.observableArrayList(applicationState.getMenuService().getAllMenuItems());
        ObservableList<Ingredient> ingredients = FXCollections.observableArrayList(applicationState.getInventoryService().getAllIngredients());

        Label titleLabel = new Label("Manager Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Manage menu items and keep inventory stocked.");

        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(event -> controller.showLoginScreen());

        Button homeButton = new Button("Home");
        homeButton.setOnAction(event -> controller.returnToHome());

        HBox headerActions = new HBox(10, homeButton, logoutButton);
        headerActions.setAlignment(Pos.CENTER_RIGHT);

        BorderPane header = new BorderPane();
        header.setLeft(new VBox(6, titleLabel, subtitleLabel));
        header.setRight(headerActions);

        ListView<MenuItem> menuListView = new ListView<>(menuItems);
        menuListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String text = item.getName();
                if (item instanceof Pastry pastry) {
                    text += " (" + pastry.getVariation() + ")";
                }
                setText(text + " - $" + String.format("%.2f", item.getBasePrice()));
            }
        });
        menuListView.setPrefHeight(320);

        Label selectedNameLabel = new Label("Name: No item selected");
        Label selectedTypeLabel = new Label("Type: -");
        Label selectedPriceLabel = new Label("Base Price: -");
        Label selectedAvailabilityLabel = new Label("Available: -");

        TextField editNameField = new TextField();
        editNameField.setPromptText("Selected item name");
        editNameField.setDisable(true);

        TextField editBasePriceField = new TextField();
        editBasePriceField.setPromptText("Selected base price");
        editBasePriceField.setDisable(true);

        TextField editVariationField = new TextField();
        editVariationField.setPromptText("Selected pastry variation");
        editVariationField.setDisable(true);

        Button saveSelectedButton = new Button("Save Selected Edits");
        saveSelectedButton.setDisable(true);

        menuListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                selectedNameLabel.setText("Name: No item selected");
                selectedTypeLabel.setText("Type: -");
                selectedPriceLabel.setText("Base Price: -");
                selectedAvailabilityLabel.setText("Available: -");
                editNameField.clear();
                editBasePriceField.clear();
                editVariationField.clear();
                editNameField.setDisable(true);
                editBasePriceField.setDisable(true);
                editVariationField.setDisable(true);
                saveSelectedButton.setDisable(true);
                return;
            }

            selectedNameLabel.setText("Name: " + newValue.getName());
            selectedTypeLabel.setText("Type: " + (newValue instanceof Beverage ? "Beverage" : "Pastry"));
            selectedPriceLabel.setText("Base Price: $" + String.format("%.2f", newValue.getBasePrice()));
            boolean available = applicationState.getInventoryService().isAvailable(newValue);
            selectedAvailabilityLabel.setText("Available: " + (available ? "Yes" : "No"));
            editNameField.setText(newValue.getName());
            editBasePriceField.setText(String.format("%.2f", newValue.getBasePrice()));
            editNameField.setDisable(false);
            editBasePriceField.setDisable(false);
            saveSelectedButton.setDisable(false);

            if (newValue instanceof Pastry pastry) {
                editVariationField.setText(pastry.getVariation());
                editVariationField.setDisable(false);
            } else {
                editVariationField.clear();
                editVariationField.setDisable(true);
            }
        });

        ChoiceBox<String> itemTypeChoice = new ChoiceBox<>(FXCollections.observableArrayList("Beverage", "Pastry"));
        itemTypeChoice.setValue("Beverage");

        TextField itemNameField = new TextField();
        itemNameField.setPromptText("Item name");

        TextField basePriceField = new TextField();
        basePriceField.setPromptText("Base price");

        TextField variationField = new TextField();
        variationField.setPromptText("Variation (pastries)");

        Button addItemButton = new Button("Add Item");
        Button removeItemButton = new Button("Remove Selected");

        TableView<Ingredient> inventoryTable = new TableView<>(ingredients);
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Ingredient, String> ingredientNameColumn = new TableColumn<>("Ingredient");
        ingredientNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));

        TableColumn<Ingredient, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(String.format("%.2f", cell.getValue().getQuantity()))
        );

        TableColumn<Ingredient, String> unitColumn = new TableColumn<>("Unit");
        unitColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUnit()));

        inventoryTable.getColumns().setAll(ingredientNameColumn, quantityColumn, unitColumn);
        inventoryTable.setPrefHeight(320);

        TextField restockAmountField = new TextField();
        restockAmountField.setPromptText("Restock quantity");

        Label statusLabel = new Label("Use the manager tools to update the menu or inventory.");
        statusLabel.setWrapText(true);

        addItemButton.setOnAction(event -> {
            controller.addMenuItem(
                    itemTypeChoice.getValue(),
                    itemNameField.getText(),
                    basePriceField.getText(),
                    variationField.getText(),
                    menuItems,
                    statusLabel
            );
            itemNameField.clear();
            basePriceField.clear();
            variationField.clear();
        });

        removeItemButton.setOnAction(event ->
                controller.removeMenuItem(menuListView.getSelectionModel().getSelectedItem(), menuItems, statusLabel)
        );

        saveSelectedButton.setOnAction(event ->
                controller.updateMenuItem(
                        menuListView.getSelectionModel().getSelectedItem(),
                        editNameField.getText(),
                        editBasePriceField.getText(),
                        editVariationField.getText(),
                        menuItems,
                        statusLabel
                )
        );

        Button restockButton = new Button("Restock Selected");
        restockButton.setOnAction(event -> {
            controller.restockIngredient(
                    inventoryTable.getSelectionModel().getSelectedItem(),
                    restockAmountField.getText(),
                    ingredients,
                    statusLabel
            );
            restockAmountField.clear();
            inventoryTable.refresh();
        });

        VBox menuSection = new VBox(
                12,
                new Label("Menu"),
                menuListView,
                selectedNameLabel,
                selectedTypeLabel,
                selectedPriceLabel,
                selectedAvailabilityLabel,
                new Label("Add New Item"),
                itemTypeChoice,
                itemNameField,
                basePriceField,
                variationField,
                addItemButton,
                new Label("Edit Selected Item"),
                editNameField,
                editBasePriceField,
                editVariationField,
                new HBox(10, saveSelectedButton, removeItemButton)
        );
        menuSection.setPadding(new Insets(18));
        menuSection.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 12;");

        VBox inventorySection = new VBox(
                12,
                new Label("Inventory"),
                inventoryTable,
                restockAmountField,
                restockButton
        );
        inventorySection.setPadding(new Insets(18));
        inventorySection.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 12;");

        HBox content = new HBox(18, menuSection, inventorySection);
        HBox.setHgrow(menuSection, Priority.ALWAYS);
        HBox.setHgrow(inventorySection, Priority.ALWAYS);
        menuSection.setMaxWidth(Double.MAX_VALUE);
        inventorySection.setMaxWidth(Double.MAX_VALUE);

        BorderPane layout = new BorderPane();
        layout.setTop(header);
        layout.setCenter(content);
        layout.setBottom(statusLabel);
        BorderPane.setMargin(header, new Insets(0, 0, 18, 0));
        BorderPane.setMargin(content, new Insets(0, 0, 18, 0));
        BorderPane.setMargin(statusLabel, new Insets(8, 0, 0, 0));
        layout.setPadding(new Insets(24));

        return layout;
    }
}
