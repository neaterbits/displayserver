package com.neaterbits.displayserver.xwindows.processing;

import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public interface XConnectionOps {

    void sendReply(XReply reply);

    void sendError(com.neaterbits.displayserver.protocol.messages.XError error);

    void sendEvent(XEvent event);

    CARD16 getSequenceNumber();
}
