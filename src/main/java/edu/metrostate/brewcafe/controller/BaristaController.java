package edu.metrostate.brewcafe.controller;

import edu.metrostate.brewcafe.model.Order;
import edu.metrostate.brewcafe.model.OrderStatus;
import edu.metrostate.brewcafe.model.UserRole;
import edu.metrostate.brewcafe.service.CafeApplicationState;
import edu.metrostate.brewcafe.view.BaristaLoginView;
import javafx.scene.layout.BorderPane;

public class BaristaController {

    private final BorderPane rootLayout;
    private final CafeApplicationState applicationState;

    public BaristaController(BorderPane rootLayout, CafeApplicationState applicationState) {
        this.rootLayout = rootLayout;
        this.applicationState = applicationState;
    }

    public boolean authenticate(String username, String password) {
        return applicationState.getAuthService()
                .authenticate(username.trim(), password, UserRole.BARISTA)
                .isPresent();
    }

    // Delegates to the OrderService to physically move the data, then saves it
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        if (order == null) return;

        if (newStatus == OrderStatus.FULFILLED) {
            // Moves the order from pending to fulfilled list
            applicationState.getOrderService().completeOrder(order.getId());
        } else {
            // Just updates the status in the pending list
            applicationState.getOrderService().updateOrderStatus(order.getId(), newStatus);
        }

        try {
            applicationState.saveOrderData();
        } catch (Exception e) {
            System.err.println("Failed to save order data: " + e.getMessage());
        }
    }

    // Reused from Andrew
    public void showLoginScreen() {
        rootLayout.setCenter(new BaristaLoginView(rootLayout, applicationState).build());
    }

    public void returnToHome() {
        new MainMenuController(rootLayout, applicationState).returnToHome();
    }
}