package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.enums.RevertTo;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.GetInputFocusReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetInputFocus;
import com.neaterbits.displayserver.protocol.messages.requests.SetInputFocus;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreFocusMessageProcessor extends XOpCodeProcessor {

    public XCoreFocusMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.SET_INPUT_FOCUS,
                OpCodes.GET_INPUT_FOCUS
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
        
        case OpCodes.SET_INPUT_FOCUS: {
            
            log(messageLength, opcode, sequenceNumber, SetInputFocus.decode(stream));
            
            break;
        }
        
        case OpCodes.GET_INPUT_FOCUS: {
            
            log(messageLength, opcode, sequenceNumber, GetInputFocus.decode(stream));
            
            sendReply(client, new GetInputFocusReply(sequenceNumber, RevertTo.None, WINDOW.None));
            
            break;
        }
        
        }
    }
}
