package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.WindowException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.ClearArea;
import com.neaterbits.displayserver.protocol.messages.requests.CopyArea;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public final class XCoreAreaMessageProcessor extends BaseXCorePixmapRenderProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XPixmapsConstAccess xPixmaps;
    
    
    public XCoreAreaMessageProcessor(
            XWindowsServerProtocolLog protocolLog,
            XWindowsConstAccess<?> xWindows,
            XPixmapsConstAccess xPixmaps) {

        super(protocolLog, xPixmaps);

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

            try {
                final XWindow xWindow = findClientOrRootWindow(xWindows, clearArea.getWindow());
                
                final int width;
                final int height;
                
                if (clearArea.getWidth().getValue() == 0) {
                    width = xWindow.getWidth() - clearArea.getX().getValue();
                }
                else {
                    width = clearArea.getWidth().getValue();
                }
    
                if (clearArea.getHeight().getValue() == 0) {
                    height = xWindow.getHeight() - clearArea.getY().getValue();
                }
                else {
                    height = clearArea.getHeight().getValue();
                }
                
                renderWindowBackground(
                        xWindow,
                        clearArea.getX().getValue(),
                        clearArea.getY().getValue(),
                        width,
                        height);
            }
            catch (WindowException ex) {
                sendError(client, Errors.Window, sequenceNumber, ex.getWindow().getValue(), opcode);
            }
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
