package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.FreeGC;
import com.neaterbits.displayserver.protocol.messages.requests.SetClipRectangles;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public class XCoreGCMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XPixmapsConstAccess xPixmaps;
    
    public XCoreGCMessageProcessor(XWindowsServerProtocolLog protocolLog, XWindowsConstAccess<?> xWindows,
            XPixmapsConstAccess xPixmaps) {
        super(protocolLog);
    
        this.xWindows = xWindows;
        this.xPixmaps = xPixmaps;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.CREATE_GC,
                OpCodes.CHANGE_GC,
                OpCodes.SET_CLIP_RECTANGLES,
                OpCodes.FREE_GC
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
        case OpCodes.CREATE_GC: {
            
            final CreateGC createGC = log(messageLength, opcode, sequenceNumber, CreateGC.decode(stream));
            
            try {
                final DRAWABLE drawable = createGC.getDrawable();
                
                findDrawable(xWindows, xPixmaps, drawable);
                
                client.createGC(createGC);
                
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
            catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.CHANGE_GC: {
            
            final ChangeGC changeGC = log(messageLength, opcode, sequenceNumber, ChangeGC.decode(stream));
            
            try {
                client.changeGC(changeGC);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, changeGC.getGc().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.SET_CLIP_RECTANGLES: {
            
            log(messageLength, opcode, sequenceNumber, SetClipRectangles.decode(stream));
            
            break;
        }
        
        case OpCodes.FREE_GC: {
            
            final FreeGC freeGC = log(messageLength, opcode, sequenceNumber, FreeGC.decode(stream));

            try {
                client.freeGC(freeGC);
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            }
            break;
        }
        }
    }
}
