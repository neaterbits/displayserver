package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.CARD16;

public interface XWindowsServerProtocolLog {

    void onReceivedRequest(int messageLength, int opcode, CARD16 sequenceNumber, XRequest request);
    
    void onSendEvent(XEvent event);
    
    void onSendReply(XReply reply);
    
    void onSendError(com.neaterbits.displayserver.protocol.messages.XError error);
}
