package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.messages.events.types.EventState;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public interface XInputEventHandlerConstAccess {

    EventState getEventState(WINDOW window);
    
}
