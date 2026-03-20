package edu.metrostate.brewcafe.service;

public interface CafeSubject {
    void addObserver(CafeObserver observer);

    void removeObserver(CafeObserver observer);

    void notifyObservers(String eventName);
}
