package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.Event;

public interface XWindowsEventListener {

    void onEvent(Event event);
    
}
