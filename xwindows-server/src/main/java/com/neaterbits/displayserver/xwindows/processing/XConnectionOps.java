package com.neaterbits.displayserver.xwindows.processing;

import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public interface XConnectionOps {

    void sendReply(Reply reply);

    void sendError(com.neaterbits.displayserver.protocol.messages.Error error);

    void sendEvent(Event event);

    CARD16 getSequenceNumber();
}
