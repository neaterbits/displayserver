package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public final class XWindowsClientProtocolLogImpl extends BaseLogImpl implements XWindowsClientProtocolLog {

    public XWindowsClientProtocolLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public void onSendRequest(int messageLength, Request request) {
        debug("onSendRequest",
                "messageLength", messageLength,
                "opcode", request.getOpCode(),
                "request", request.toDebugString());
    }

    @Override
    public void onRecivedEvent(Event event) {
        debug("onReceivedEvent", "event", event.toDebugString());
    }

    @Override
    public void onReceivedReply(Reply reply) {
        debug("onReceivedReply", "reply", reply.toDebugString());
    }

    @Override
    public void onReceivedError(Error error) {
        info("onReceivedError", "error", error.toDebugString());
    }
}
