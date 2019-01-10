package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;

public interface XWindowsMessaging extends XWindowsRequestSender {

    ServerMessage getServerMessage();
    
    void close() throws Exception;
}
