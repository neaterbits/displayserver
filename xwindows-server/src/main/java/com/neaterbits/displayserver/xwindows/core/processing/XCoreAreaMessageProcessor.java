package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.ClearArea;
import com.neaterbits.displayserver.protocol.messages.requests.CopyArea;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreAreaMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XPixmapsConstAccess xPixmaps;
    
    
    public XCoreAreaMessageProcessor(
            XWindowsServerProtocolLog protocolLog,
            XWindowsConstAccess<?> xWindows,
            XPixmapsConstAccess xPixmaps) {

        super(protocolLog);

        this.xWindows = xWindows;
        this.xPixmaps = xPixmaps;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.CLEAR_AREA,
                OpCodes.COPY_AREA
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
        case OpCodes.CLEAR_AREA: {
            
            final ClearArea clearArea = log(messageLength, opcode, sequenceNumber, ClearArea.decode(stream));
            
            break;
        }
        
        case OpCodes.COPY_AREA: {
            
            final CopyArea copyArea = log(messageLength, opcode, sequenceNumber, CopyArea.decode(stream));
            
            try {
                final BufferOperations src = getBufferOperations(xWindows, xPixmaps, copyArea.getSrcDrawable());
                final BufferOperations dst = getBufferOperations(xWindows, xPixmaps, copyArea.getDstDrawable());
                
                client.getGC(copyArea.getGC());
                
                dst.copyArea(
                        src,
                        copyArea.getSrcX().getValue(), copyArea.getSrcY().getValue(),
                        copyArea.getDstX().getValue(), copyArea.getDstY().getValue(),
                        copyArea.getWidth().getValue(), copyArea.getHeight().getValue());
                
            } catch (GContextException ex) {
                sendError(client, Errors.GContext, sequenceNumber, ex.getGContext().getValue(), opcode);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
            break;
        }
        }
    }
}
