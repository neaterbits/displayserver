package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;

public interface XWindowsProtocolLog {

    void onReceivedRequest(int messageLength, int opcode, Request request);
    
    void onSendReply(Reply reply);
}
