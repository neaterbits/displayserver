package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.GetSelectionOwnerReply;
import com.neaterbits.displayserver.protocol.messages.requests.ConvertSelection;
import com.neaterbits.displayserver.protocol.messages.requests.GetSelectionOwner;
import com.neaterbits.displayserver.protocol.messages.requests.SetSelectionOwner;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreSelectionMessageProcessor extends XOpCodeProcessor {
    
    private final Map<ATOM, WINDOW> selectionOwners;
    
    public XCoreSelectionMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
        
        this.selectionOwners = new HashMap<>();
    }

    @Override
    protected int[] getOpCodes() {
        
        return new int [] {
                OpCodes.SET_SELECTION_OWNER,
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
        
        case OpCodes.SET_SELECTION_OWNER: {

            final SetSelectionOwner setSelectionOwner = log(messageLength, opcode, sequenceNumber, SetSelectionOwner.decode(stream));
            
            selectionOwners.put(setSelectionOwner.getSelection(), setSelectionOwner.getOwner());
            break;
        }
        
        case OpCodes.GET_SELECTION_OWNER: {
            
            final GetSelectionOwner getSelectionOwner = log(messageLength, opcode, sequenceNumber, GetSelectionOwner.decode(stream));
            
            final WINDOW selectionOwner = selectionOwners.get(getSelectionOwner.getSelection());
            
            final GetSelectionOwnerReply reply = new GetSelectionOwnerReply(
                    sequenceNumber,
                    selectionOwner != null ? selectionOwner : WINDOW.None);
            
            sendReply(client, reply);
            break;
        }
        
        case OpCodes.CONVERT_SELECTION: {
            
            log(messageLength, opcode, sequenceNumber, ConvertSelection.decode(stream));
            
            break;
        }
        
        }
    }
}
