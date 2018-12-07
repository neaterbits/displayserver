package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;

public interface XWindowsProtocolLog {

    void onReceivedRequest(int messageLength, int opcode, CARD16 sequenceNumber, Request request);
    
    void onSendEvent(Event event);
    
    void onSendReply(Reply reply);
    
    void onSendError(com.neaterbits.displayserver.protocol.messages.Error error);
}
