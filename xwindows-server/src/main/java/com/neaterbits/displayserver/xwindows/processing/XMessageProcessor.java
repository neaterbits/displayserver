package com.neaterbits.displayserver.xwindows.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.types.CARD16;

public abstract class XMessageProcessor extends XMessageSender {

    protected XMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
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
}
