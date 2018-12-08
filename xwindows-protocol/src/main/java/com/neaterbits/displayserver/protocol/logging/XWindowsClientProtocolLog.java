package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;

public interface XWindowsClientProtocolLog {

    void onSendRequest(int messageLength, Request request);
    
    void onRecivedEvent(Event event);
    
    void onReceivedReply(Reply reply);
    
    void onReceivedError(com.neaterbits.displayserver.protocol.messages.Error error);

}
