package com.neaterbits.displayserver.events.common;

public interface EventSource {
	
    void registerEventListener(EventListener eventListener);
    
    void deregisterEventListener(EventListener eventListener);
}
