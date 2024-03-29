package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.events.MotionNotify;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public final class XWindowsServerProtocolLogImpl extends BaseLogImpl implements XWindowsServerProtocolLog {

    public XWindowsServerProtocolLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public final void onReceivedRequest(int messageLength, int opcode, CARD16 sequenceNumber, XRequest request) {
        debug("onReceivedRequest",
                "messageLength", messageLength,
                "opcode", opcode,
                "seq", sequenceNumber,
                "request", request.toDebugString());
    }

    
    @Override
    public void onSendEvent(XEvent event) {
        
        if (!(event instanceof MotionNotify)) {
            debug("sendEvent", "event", event.toDebugString());
        }
    }

    @Override
    public void onSendReply(XReply reply) {
        debug("sendReply", "reply", reply.toDebugString());
    }

    @Override
    public void onSendError(com.neaterbits.displayserver.protocol.messages.XError error) {
        info("sendError", "error", error.toDebugString());
    }
}
