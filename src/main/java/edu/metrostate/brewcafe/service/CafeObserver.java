package edu.metrostate.brewcafe.service;

public interface CafeObserver {
    void onCafeStateChanged(String eventName);
}
