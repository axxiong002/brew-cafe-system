package edu.metrostate.brewcafe.model;

// Describes how much of an ingredient a menu item consumes when prepared.
public record IngredientUsage(String ingredientId, double quantityRequired) {
}
