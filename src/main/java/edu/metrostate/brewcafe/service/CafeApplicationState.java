package edu.metrostate.brewcafe.service;

import edu.metrostate.brewcafe.model.User;
import edu.metrostate.brewcafe.persistence.JsonDataLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// Holds shared service instances so every screen talks to the same application state.
public class CafeApplicationState {
    private final AuthService authService;
    private final OrderService orderService;

    public CafeApplicationState(AuthService authService, OrderService orderService) {
        this.authService = authService;
        this.orderService = orderService;
    }

    public static CafeApplicationState fromDefaultData() throws IOException {
        JsonDataLoader dataLoader = new JsonDataLoader();
        List<User> users = dataLoader.loadUsers(Path.of("data", "users.json"));
        return new CafeApplicationState(new AuthService(users), new OrderService());
    }

    public AuthService getAuthService() {
        return authService;
    }

    public OrderService getOrderService() {
        return orderService;
    }
}
