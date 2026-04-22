package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderStatus;
import edu.metrostate.brewcafe.service.CafeObserver;
import edu.metrostate.brewcafe.service.OrderService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BaristaDashboardView implements CafeObserver {
    private final BorderPane rootLayout;
    private final OrderService orderService;

    private final ObservableList<Order> observablePendingOrders = FXCollections.observableArrayList();
    private final ObservableList<Order> observableFulfilledOrders = FXCollections.observableArrayList();

    public BaristaDashboardView(BorderPane rootLayout, OrderService orderService) {
        this.rootLayout = rootLayout;
        this.orderService = orderService;

        // Register to listen for new orders
        this.orderService.addObserver(this);

        refreshOrderList();
    }

    public Parent build() {
        BorderPane dashboard = new BorderPane();
        dashboard.setStyle("-fx-background-color: #f4f4f4;");

        // Right Side: The Detail Area
        VBox detailArea = new VBox(15);
        detailArea.setPadding(new Insets(20));
        detailArea.setAlignment(Pos.TOP_LEFT);
        detailArea.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");

        //  Left Side: Pending (Top) and Fulfilled (Bottom)
        ListView<Order> pendingList = new ListView<>(observablePendingOrders);
        setupOrderListView(pendingList);

        ListView<Order> fulfilledList = new ListView<>(observableFulfilledOrders);
        setupOrderListView(fulfilledList);

        Label pendingLabel = new Label("Pending Orders");
        pendingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label fulfilledLabel = new Label("Fulfilled Orders");
        fulfilledLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        VBox leftPanel = new VBox(10, pendingLabel, pendingList, fulfilledLabel, fulfilledList);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(250);
        VBox.setVgrow(pendingList, Priority.ALWAYS);
        VBox.setVgrow(fulfilledList, Priority.ALWAYS);

        pendingList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fulfilledList.getSelectionModel().clearSelection();
                showOrderDetails(detailArea, newVal, true);
            }
        });

        fulfilledList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pendingList.getSelectionModel().clearSelection();
                showOrderDetails(detailArea, newVal, false);
            }
        });

        dashboard.setLeft(leftPanel);
        dashboard.setCenter(detailArea);

        return dashboard;
    }


    private void showOrderDetails(VBox detailArea, Order order, boolean showFulfillButton) {
        detailArea.getChildren().clear();

        // Header Information
        Label idLabel = new Label("Order ID: " + order.getId());
        idLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label customerLabel = new Label("Customer: " + order.getCustomerName());
        customerLabel.setStyle("-fx-font-size: 18px;");

        Label statusLabel = new Label("Status: " + order.getStatus());
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6f4e37; -fx-font-weight: bold;");

        detailArea.getChildren().addAll(idLabel, customerLabel, statusLabel);

        // Order Items List
        Label itemsHeader = new Label("Items:");
        itemsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 0 5 0;");
        detailArea.getChildren().add(itemsHeader);

        double orderTotal = 0.0;

        if (order.getItems() == null || order.getItems().isEmpty()) {
            detailArea.getChildren().add(new Label("  No items found."));
        } else {
            for (edu.metrostate.brewcafe.model.OrderItem item : order.getItems()) {
                edu.metrostate.brewcafe.model.MenuItem menuItem = item.getMenuItem();

                String displayName = menuItem.getName();

                // If it's a pastry, attach the variation
                if (menuItem instanceof edu.metrostate.brewcafe.model.Pastry) {
                    edu.metrostate.brewcafe.model.Pastry pastry = (edu.metrostate.brewcafe.model.Pastry) menuItem;
                    displayName = pastry.getVariation() + " " + displayName;
                }

                // Calculate the line item price (price * quantity)
                double itemTotal = menuItem.getBasePrice() * item.getQuantity();
                orderTotal += itemTotal;

                // Format it nicely: "Example: 2x Blueberry Muffin - $6.90"
                String itemText = String.format("  %dx %s - $%.2f", item.getQuantity(), displayName, itemTotal);
                Label itemLabel = new Label(itemText);
                itemLabel.setStyle("-fx-font-size: 16px;");
                detailArea.getChildren().add(itemLabel);
            }

            // Show the total price at the bottom of the list
            Label totalLabel = new Label(String.format("\nTotal: $%.2f", orderTotal));
            totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            detailArea.getChildren().add(totalLabel);
        }

        // Spacing before the button
        Region spacer = new Region();
        spacer.setMinHeight(20);
        detailArea.getChildren().add(spacer);

        // The Action Button
        if (showFulfillButton) {
            // Show different button depending on current status
            if (order.getStatus() == OrderStatus.PENDING) {
                Button acceptBtn = new Button("Accept Order");
                acceptBtn.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 8 15 8 15;");
                acceptBtn.setOnAction(e -> {
                    order.setStatus(OrderStatus.IN_PROGRESS);
                    refreshOrderList();
                    showOrderDetails(detailArea, order, true);
                });
                detailArea.getChildren().add(acceptBtn);

            } else if (order.getStatus() == OrderStatus.IN_PROGRESS) {
                Button readyBtn = new Button("Mark as Ready");
                readyBtn.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 8 15 8 15;");
                readyBtn.setOnAction(e -> {
                    order.setStatus(OrderStatus.READY_FOR_PICKUP);
                    refreshOrderList();
                    showOrderDetails(detailArea, order, true);
                });
                detailArea.getChildren().add(readyBtn);

            } else if (order.getStatus() == OrderStatus.READY_FOR_PICKUP) {
                Button fulfillBtn = new Button("Complete Order");
                fulfillBtn.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 8 15 8 15;");
                fulfillBtn.setOnAction(e -> {
                    order.setStatus(OrderStatus.FULFILLED);
                    refreshOrderList();
                    detailArea.getChildren().clear();
                });
                detailArea.getChildren().add(fulfillBtn);
            }
        }
    }

    // Helper method to format how items look in the lists
    private void setupOrderListView(ListView<Order> listView) {
        listView.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Order " + item.getId() + " - " + item.getCustomerName());
                }
            }
        });
    }

    @Override
    public void onCafeStateChanged(String eventName) {
        if ("order-added".equals(eventName)) {
            Platform.runLater(this::refreshOrderList);
        }
    }

    // Sorts the real data into the two UI lists safely
    private void refreshOrderList() {
        observablePendingOrders.clear();
        observableFulfilledOrders.clear();

        for (Order order : orderService.getPendingOrders()) {
            // ONLY completed orders go to the bottom history list
            if (order.getStatus() == OrderStatus.FULFILLED) {
                observableFulfilledOrders.add(order);
            } else {
                // PENDING, IN_PROGRESS, and READY_FOR_PICKUP all stay in the top list
                observablePendingOrders.add(order);
            }
        }
    }
}