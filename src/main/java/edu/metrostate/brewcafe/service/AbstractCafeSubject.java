package edu.metrostate.brewcafe.service;

import java.util.ArrayList;
import java.util.List;

// Shared observer helper so services can publish updates without duplicating logic.
public abstract class AbstractCafeSubject implements CafeSubject {
    private final List<CafeObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(CafeObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(CafeObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String eventName) {
        // This is the starter for the required Observer pattern.
        for (CafeObserver observer : observers) {
            observer.onCafeStateChanged(eventName);
        }
    }
}
