package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.XEvent;

public interface XWindowsEventListener {

    void onEvent(XEvent event);
    
}
