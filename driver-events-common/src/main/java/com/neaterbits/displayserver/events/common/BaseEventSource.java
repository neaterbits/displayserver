package com.neaterbits.displayserver.events.common;

import com.neaterbits.displayserver.driver.common.Listeners;

public abstract class BaseEventSource implements EventSource {

    private final Listeners<EventListener> eventListeners;
    
    protected BaseEventSource() {
        this.eventListeners = new Listeners<>();
    }

    @Override
    public final void registerEventListener(EventListener eventListener) {
        
        eventListeners.registerListener(eventListener);
    }

    @Override
    public final void deregisterEventListener(EventListener eventListener) {

        eventListeners.deregisterListener(eventListener);
    }
    
    protected final void triggerEvent(Event event) {
        eventListeners.triggerEvent(event, EventListener::onEvent);
    }
}
