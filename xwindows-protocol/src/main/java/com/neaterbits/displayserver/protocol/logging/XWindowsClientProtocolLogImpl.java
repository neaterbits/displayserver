package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public final class XWindowsClientProtocolLogImpl extends BaseLogImpl implements XWindowsClientProtocolLog {

    public XWindowsClientProtocolLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public void onInitialMessageError(byte errorCode, int sequenceNumber, String reason) {
        
        debug("onInitialMessageError",
                "errorCode", errorCode,
                "sequenceNumber", sequenceNumber,
                "reason", reason
        );
        
    }

    @Override
    public void onSendRequest(XRequest request) {
        debug("onSendRequest",
                "opcode", request.getOpCode(),
                "request", request.toDebugString());
    }

    @Override
    public void onSentRequest(int messageLength, int sequenceNumber, XRequest request) {
        debug("onSentRequest",
                "messageLength", messageLength,
                "opcode", request.getOpCode(),
                "seq", sequenceNumber,
                "request", request.toDebugString());
    }

    @Override
    public void onRecivedEvent(XEvent event) {
        debug("onReceivedEvent", "event", event.toDebugString());
    }

    @Override
    public void onReceivedReply(XReply reply) {
        debug("onReceivedReply", "reply", reply.toDebugString());
    }

    @Override
    public void onReceivedError(XError error) {
        info("onReceivedError",
                "error", error.toDebugString());
    }
}
