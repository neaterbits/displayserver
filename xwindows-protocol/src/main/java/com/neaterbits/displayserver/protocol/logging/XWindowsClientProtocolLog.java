package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;

public interface XWindowsClientProtocolLog {

    void onInitialMessageError(byte errorCode, int sequenceNumber, String reason);

    void onSendRequest(XRequest request);

    void onSentRequest(int messageLength, int sequenceNumber, XRequest request);
    
    void onRecivedEvent(XEvent event);
    
    void onReceivedReply(XReply reply);
    
    void onReceivedError(com.neaterbits.displayserver.protocol.messages.XError error);

}
