package com.neaterbits.displayserver.xwindows.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public abstract class XMessageProcessor {

    private final XWindowsServerProtocolLog protocolLog;

    protected XMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        this.protocolLog = protocolLog;
    }

    public final void processMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException {
        
        onMessage(stream, messageLength, opcode, sequenceNumber, client);
    }
    
    protected abstract void onMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException;
    
    protected final <T extends XRequest> T log(int messageLength, int opcode, CARD16 sequenceNumber, T request) {
        
        if (protocolLog != null) {
            protocolLog.onReceivedRequest(messageLength, opcode, sequenceNumber, request);
        }

        return request;
    }
    
    protected final void sendEvent(XClientOps client, WINDOW window, XEvent event) {
        
        if (protocolLog != null) {
            protocolLog.onSendEvent(event);
        }
        
        client.sendEvent(event);
    }

    protected final void sendReply(XClientOps client, XReply reply) {
        
        if (protocolLog != null) {
            protocolLog.onSendReply(reply);
        }
        
        client.sendReply(reply);
    }

    protected final void sendError(XClientOps client, BYTE errorCode, CARD16 sequenceNumber, long value, int opcode) {
        
        final XError error = new XError(errorCode, sequenceNumber, new CARD32(value), new CARD8((short)opcode));
        
        if (protocolLog != null) {
            protocolLog.onSendError(error);
        }
        
        client.sendError(error);
    }
}
