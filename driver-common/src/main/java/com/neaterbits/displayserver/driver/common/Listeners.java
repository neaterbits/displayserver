package com.neaterbits.displayserver.driver.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class Listeners<T> {

    private final List<T> listeners;
    
    public Listeners() {
        this.listeners = new ArrayList<>();
    }
    
    public void registerListener(T listener) {
        
        Objects.requireNonNull(listener);
        
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException();
        }
        
        listeners.add(listener);
    }
    
    public void deregisterListener(T listener) {

        Objects.requireNonNull(listener);
        
        listeners.remove(listener);
    }

    public <E> void triggerEvent(E event, BiConsumer<T, E> triggerEvent) {
        
        for (T listener : listeners) {
            triggerEvent.accept(listener, event);
        }
    }
}
