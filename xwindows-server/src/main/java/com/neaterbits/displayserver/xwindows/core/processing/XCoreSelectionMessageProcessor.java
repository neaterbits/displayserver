package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.GetSelectionOwnerReply;
import com.neaterbits.displayserver.protocol.messages.requests.ConvertSelection;
import com.neaterbits.displayserver.protocol.messages.requests.GetSelectionOwner;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreSelectionMessageProcessor extends XOpCodeProcessor {
    
    public XCoreSelectionMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
    }

    @Override
    protected int[] getOpCodes() {
        
        return new int [] {
                OpCodes.GET_SELECTION_OWNER,
                OpCodes.CONVERT_SELECTION
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
        
        case OpCodes.GET_SELECTION_OWNER: {
            
            log(messageLength, opcode, sequenceNumber, GetSelectionOwner.decode(stream));
            
            sendReply(client, new GetSelectionOwnerReply(sequenceNumber, WINDOW.None));
            break;
        }
        
        case OpCodes.CONVERT_SELECTION: {
            
            log(messageLength, opcode, sequenceNumber, ConvertSelection.decode(stream));
            
            break;
        }
        
        }
    }
}
