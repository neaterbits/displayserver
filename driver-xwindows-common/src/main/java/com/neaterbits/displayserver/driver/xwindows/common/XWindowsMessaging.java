package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;

public interface XWindowsMessaging extends XWindowsRequestSender {

    int sendInitialMessage(ClientMessage initialMessage) throws IOException;

    ServerMessage getServerMessage();
    
    void close() throws Exception;
}
