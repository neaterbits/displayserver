package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.CreateCursor;
import com.neaterbits.displayserver.protocol.messages.requests.RecolorCursor;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.CreateGlyphCursor;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.FreeCursor;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.model.XCursor;
import com.neaterbits.displayserver.xwindows.model.XCursors;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreCursorMessageProcessor extends XOpCodeProcessor {

    private final XCursors xCursors;
    
    public XCoreCursorMessageProcessor(XWindowsServerProtocolLog protocolLog, XCursors xCursors) {
        super(protocolLog);
        
        this.xCursors = xCursors;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.CREATE_CURSOR,
                OpCodes.CREATE_GLYPH_CURSOR,
                OpCodes.FREE_CURSOR,
                OpCodes.RECOLOR_CURSOR
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
        case OpCodes.CREATE_CURSOR: {
            
            final CreateCursor createCursor = log(messageLength, opcode, sequenceNumber, CreateCursor.decode(stream));
            
            try {
                client.checkAndAddResourceId(createCursor.getCID());

                xCursors.add(createCursor.getCID(), new XCursor());
            } catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, createCursor.getCID().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.CREATE_GLYPH_CURSOR: {
            
            log(messageLength, opcode, sequenceNumber, CreateGlyphCursor.decode(stream));
            
            break;
        }
        
        case OpCodes.FREE_CURSOR: {
            
            final FreeCursor freeCursor = log(messageLength, opcode, sequenceNumber, FreeCursor.deccode(stream));
            
            client.checkAndRemoveResourceId(freeCursor.getCursor());
            
            if (!xCursors.remove(freeCursor.getCursor())) {
                sendError(client, Errors.Cursor, sequenceNumber, freeCursor.getCursor().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.RECOLOR_CURSOR: {
            
            log(messageLength, opcode, sequenceNumber, RecolorCursor.decode(stream));
        
            break;
        }
        
        }
    }

    
}
