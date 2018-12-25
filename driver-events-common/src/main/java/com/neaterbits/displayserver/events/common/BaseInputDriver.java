package com.neaterbits.displayserver.events.common;

import com.neaterbits.displayserver.driver.common.Listeners;

public abstract class BaseInputDriver implements InputDriver {

    private final Listeners<InputEventListener> eventListeners;
    
    protected BaseInputDriver() {
        this.eventListeners = new Listeners<>();
    }

    @Override
    public final void registerInputEventListener(InputEventListener eventListener) {
        
        eventListeners.registerListener(eventListener);
    }

    @Override
    public final void deregisterInputEventListener(InputEventListener eventListener) {

        eventListeners.deregisterListener(eventListener);
    }
    
    protected final void triggerEvent(InputEvent event) {
        eventListeners.triggerEvent(event, InputEventListener::onEvent);
    }
}
