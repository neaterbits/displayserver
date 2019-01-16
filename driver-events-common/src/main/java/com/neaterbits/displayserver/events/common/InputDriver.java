package com.neaterbits.displayserver.events.common;

import com.neaterbits.displayserver.driver.common.Driver;

public interface InputDriver extends Driver {
    
    void pollForEvents();
    
    ModifierScancodes getModifierScancodes();
    
    KeyboardMapping getKeyboardMapping();
    
    void registerInputEventListener(InputEventListener eventListener);
    
    void deregisterInputEventListener(InputEventListener eventListener);
}
