package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public final class XWindowsProtocolLogImpl extends BaseLogImpl implements XWindowsProtocolLog {

    public XWindowsProtocolLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public final void onReceivedRequest(int messageLength, int opcode, CARD16 sequenceNumber, Request request) {
        debug("onReceivedRequest",
                "messageLength", messageLength,
                "opcode", opcode,
                "seq", sequenceNumber,
                "request", request.toDebugString());
    }

    
    @Override
    public void onSendEvent(Event event) {
        debug("sendEvent", "event", event.toDebugString());
    }

    @Override
    public void onSendReply(Reply reply) {
        debug("sendReply", "reply", reply.toDebugString());
    }

    @Override
    public void onSendError(com.neaterbits.displayserver.protocol.messages.Error error) {
        info("sendError", "error", error.toDebugString());
    }
}
