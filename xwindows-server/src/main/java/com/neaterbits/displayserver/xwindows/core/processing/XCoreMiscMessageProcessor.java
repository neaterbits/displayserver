package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.Bell;
import com.neaterbits.displayserver.protocol.messages.requests.SetCloseDownMode;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreMiscMessageProcessor extends XOpCodeProcessor {

    public XCoreMiscMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.BELL,
                OpCodes.SET_CLOSE_DOWN_MODE
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
        
        case OpCodes.BELL: {
            
            log(messageLength, opcode, sequenceNumber, Bell.decode(stream));
            
            break;
        }
        
        case OpCodes.SET_CLOSE_DOWN_MODE: {
            
            log(messageLength, opcode, sequenceNumber, SetCloseDownMode.decode(stream));
            
            break;
        }
        }
    }
}
