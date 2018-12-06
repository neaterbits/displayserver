package com.neaterbits.displayserver.protocol.logging;

import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public class XWindowsProtocolLogImpl extends BaseLogImpl implements XWindowsProtocolLog {

    public XWindowsProtocolLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public final void onReceivedRequest(int messageLength, int opcode, Request request) {
        debug("onReceivedRequest", "messageLength", messageLength, "opcode", opcode, "request", request.toDebugString());
    }

    @Override
    public void onSendReply(Reply reply) {
        debug("sendReply", "reply", reply.toDebugString());
    }
}
