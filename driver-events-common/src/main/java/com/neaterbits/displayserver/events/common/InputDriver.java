package com.neaterbits.displayserver.events.common;

import com.neaterbits.displayserver.driver.common.Driver;

public interface InputDriver extends Driver {
    
    ModifierScancodes getModifierScancodes();
    
    void registerInputEventListener(InputEventListener eventListener);
    
    void deregisterInputEventListener(InputEventListener eventListener);
}
