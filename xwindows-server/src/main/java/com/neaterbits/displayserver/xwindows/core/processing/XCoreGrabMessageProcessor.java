package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.GrabPointerStatus;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.GrabPointerReply;
import com.neaterbits.displayserver.protocol.messages.requests.GrabButton;
import com.neaterbits.displayserver.protocol.messages.requests.GrabKey;
import com.neaterbits.displayserver.protocol.messages.requests.GrabPointer;
import com.neaterbits.displayserver.protocol.messages.requests.GrabServer;
import com.neaterbits.displayserver.protocol.messages.requests.UngrabServer;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreGrabMessageProcessor extends XOpCodeProcessor {

    public XCoreGrabMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.GRAB_POINTER,
                OpCodes.GRAB_BUTTON,
                OpCodes.GRAB_KEY,
                OpCodes.GRAB_SERVER,
                OpCodes.UNGRAB_SERVER
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
        
        case OpCodes.GRAB_POINTER: {
            
            log(messageLength, opcode, sequenceNumber, GrabPointer.decode(stream));
            
            sendReply(client, new GrabPointerReply(sequenceNumber, GrabPointerStatus.Success));
            break;
        }
        
        case OpCodes.GRAB_BUTTON: {
            
            log(messageLength, opcode, sequenceNumber, GrabButton.decode(stream));
            
            break;
        }
        
        case OpCodes.GRAB_KEY: {
            
            log(messageLength, opcode, sequenceNumber, GrabKey.decode(stream));
            
            break;
        }
        
        case OpCodes.GRAB_SERVER: {
            
            log(messageLength, opcode, sequenceNumber, GrabServer.decode(stream));
            
            break;
        }

        case OpCodes.UNGRAB_SERVER: {
            
            log(messageLength, opcode, sequenceNumber, UngrabServer.decode(stream));
            
            break;
        }
        
        }
    }
}
