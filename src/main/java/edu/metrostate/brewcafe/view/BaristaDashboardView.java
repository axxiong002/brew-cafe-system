package edu.metrostate.brewcafe.view;

import edu.metrostate.brewcafe.controller.BaristaController;
import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderStatus;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.service.CafeObserver;
import edu.metrostate.brewcafe.service.OrderPricingService;
import edu.metrostate.brewcafe.service.OrderService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class BaristaDashboardView implements CafeObserver {
    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;
    private final OrderService orderService;
    private final BaristaController controller;

    private final ObservableList<Order> observablePendingOrders = FXCollections.observableArrayList();
    private final ObservableList<Order> observableFulfilledOrders = FXCollections.observableArrayList();

    public BaristaDashboardView(BorderPane rootLayout, CafeApplicationState applicationState, BaristaController controller) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
        this.orderService = applicationState.getOrderService();
        this.controller = controller;

        this.orderService.addObserver(this);
        refreshOrderList();
    }

    public Parent build() {
        BorderPane dashboard = new BorderPane();
        dashboard.setStyle("-fx-background-color: #f4f4f4;");
        dashboard.setPadding(new Insets(20));

        // Header
        Label titleLabel = new Label("Barista Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(event -> controller.showLoginScreen());

        Button homeButton = new Button("Home");
        homeButton.setOnAction(event -> controller.returnToHome());

        HBox headerActions = new HBox(10, homeButton, logoutButton);
        headerActions.setAlignment(Pos.CENTER_RIGHT);

        BorderPane header = new BorderPane();
        header.setLeft(titleLabel);
        header.setRight(headerActions);
        header.setPadding(new Insets(0, 0, 15, 0));
        dashboard.setTop(header);

        // Right Side: Detail Area
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(0, 0, 0, 10));

        Label detailsLabel = new Label("Order Details");
        detailsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        VBox detailArea = new VBox(15);
        detailArea.setPadding(new Insets(20));
        detailArea.setAlignment(Pos.TOP_LEFT);
        detailArea.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        detailArea.getChildren().add(new Label("Select an order from the list to view its details."));

        rightPanel.getChildren().addAll(detailsLabel, detailArea);
        VBox.setVgrow(detailArea, Priority.ALWAYS);

        // Left Side: Pending and Fulfilled Lists
        ListView<Order> pendingList = new ListView<>(observablePendingOrders);
        setupOrderListView(pendingList);

        ListView<Order> fulfilledList = new ListView<>(observableFulfilledOrders);
        setupOrderListView(fulfilledList);

        Label pendingLabel = new Label("Pending Orders");
        pendingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label fulfilledLabel = new Label("Fulfilled Orders");
        fulfilledLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        VBox leftPanel = new VBox(10, pendingLabel, pendingList, fulfilledLabel, fulfilledList);
        leftPanel.setPadding(new Insets(0, 10, 0, 0));
        leftPanel.setPrefWidth(300);
        VBox.setVgrow(pendingList, Priority.ALWAYS);
        VBox.setVgrow(fulfilledList, Priority.ALWAYS);

        // List View Listeners
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
        dashboard.setCenter(rightPanel);

        return dashboard;
    }


    private void showOrderDetails(VBox detailArea, Order order, boolean showFulfillButton) {
        detailArea.getChildren().clear();

        Label idLabel = new Label("Order ID: " + order.getId());
        idLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label customerLabel = new Label("Customer: " + order.getCustomerName());
        customerLabel.setStyle("-fx-font-size: 18px;");

        Label statusLabel = new Label("Status: " + order.getStatus());
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6f4e37; -fx-font-weight: bold;");

        detailArea.getChildren().addAll(idLabel, customerLabel, statusLabel);

        Label itemsHeader = new Label("Items:");
        itemsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 0 5 0;");
        detailArea.getChildren().add(itemsHeader);

        // Use OrderPricingService for accurate totals including size and customization costs
        OrderPricingService pricingService = new OrderPricingService();

        if (order.getItems() == null || order.getItems().isEmpty()) {
            detailArea.getChildren().add(new Label("  No items found."));
        } else {
            for (edu.metrostate.brewcafe.model.OrderItem item : order.getItems()) {
                edu.metrostate.brewcafe.model.MenuItem menuItem = item.getMenuItem();
                String displayName = menuItem.getName();

                // Add pastry variation
                if (menuItem instanceof edu.metrostate.brewcafe.model.Pastry pastry) {
                    displayName = pastry.getVariation() + " " + displayName;
                }

                // Add size if selected. Example: "(Large)"
                String sizeText = "";
                if (item.getSelectedSize() != null) {
                    sizeText = " (" + item.getSelectedSize().name() + ")";
                }

                // Add customizations if selected. Example "+ Oat Milk, Extra Shot"
                String customText = "";
                if (!item.getSelectedCustomizations().isEmpty()) {
                    customText = " + " + item.getSelectedCustomizations()
                            .stream()
                            .map(edu.metrostate.brewcafe.model.Customization::name)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                }

                // Use pricingService for accurate line total
                double lineTotal = pricingService.calculateLineTotal(item);

                String itemText = String.format("  %dx %s%s%s - $%.2f",
                        item.getQuantity(), displayName, sizeText, customText, lineTotal);

                Label itemLabel = new Label(itemText);
                itemLabel.setStyle("-fx-font-size: 16px;");
                detailArea.getChildren().add(itemLabel);
            }

            // Use pricingService for accurate order total
            double orderTotal = pricingService.calculateOrderTotal(order);
            Label totalLabel = new Label(String.format("\nTotal: $%.2f", orderTotal));
            totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            detailArea.getChildren().add(totalLabel);
        }

        Region spacer = new Region();
        spacer.setMinHeight(20);
        detailArea.getChildren().add(spacer);

        if (showFulfillButton) {
            if (order.getStatus() == OrderStatus.PENDING) {
                Button acceptBtn = new Button("Accept Order");
                acceptBtn.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15 8 15;");
                acceptBtn.setOnAction(e -> {
                    controller.updateOrderStatus(order, OrderStatus.IN_PROGRESS);
                    showOrderDetails(detailArea, order, true);
                });
                detailArea.getChildren().add(acceptBtn);

            } else if (order.getStatus() == OrderStatus.IN_PROGRESS) {
                Button readyBtn = new Button("Mark as Ready");
                readyBtn.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15 8 15;");
                readyBtn.setOnAction(e -> {
                    controller.updateOrderStatus(order, OrderStatus.READY_FOR_PICKUP);
                    showOrderDetails(detailArea, order, true);
                });
                detailArea.getChildren().add(readyBtn);

            } else if (order.getStatus() == OrderStatus.READY_FOR_PICKUP) {
                Button fulfillBtn = new Button("Complete Order");
                fulfillBtn.setStyle("-fx-background-color: #6f4e37; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15 8 15;");
                fulfillBtn.setOnAction(e -> {
                    controller.updateOrderStatus(order, OrderStatus.FULFILLED);
                    detailArea.getChildren().clear();
                    detailArea.getChildren().add(new Label("Select an order from the list to view its details."));
                });
                detailArea.getChildren().add(fulfillBtn);
            }
        }
    }

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
        if ("order-added".equals(eventName) || "order-status-updated".equals(eventName) || "order-fulfilled".equals(eventName)) {
            Platform.runLater(this::refreshOrderList);
        }
    }

    private void refreshOrderList() {
        observablePendingOrders.clear();
        observableFulfilledOrders.clear();

        observablePendingOrders.addAll(orderService.getPendingOrders());
        observableFulfilledOrders.addAll(orderService.getFulfilledOrders());
    }
}