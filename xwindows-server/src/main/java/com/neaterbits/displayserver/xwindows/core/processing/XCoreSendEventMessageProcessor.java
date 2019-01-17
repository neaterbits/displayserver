package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.SendEvent;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreSendEventMessageProcessor extends XOpCodeProcessor {

    public XCoreSendEventMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.SEND_EVENT
        };
    }

    @Override
    protected void onMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException {


        switch (opcode) {
        case OpCodes.SEND_EVENT: {
            
            log(messageLength, opcode, sequenceNumber, SendEvent.decode(stream));
            
            break;
        }
        
        }
    }
}
