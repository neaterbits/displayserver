package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;

public interface XWindowsMessageListener {

    void onInitialMessage(ServerMessage serverMessage);
    
}
