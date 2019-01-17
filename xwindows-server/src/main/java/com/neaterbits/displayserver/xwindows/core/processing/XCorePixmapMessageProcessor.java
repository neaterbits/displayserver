package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.DrawableException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.DisplayArea;
import com.neaterbits.displayserver.windows.WindowsDisplayArea;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.xwindows.model.XPixmap;
import com.neaterbits.displayserver.xwindows.model.XPixmaps;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCorePixmapMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XPixmaps xPixmaps;
    private final XLibRendererFactory rendererFactory;
    
    public XCorePixmapMessageProcessor(
            XWindowsServerProtocolLog protocolLog,
            XWindowsConstAccess<?> windows,
            XPixmaps pixmaps,
            XLibRendererFactory rendererFactory) {
        
        super(protocolLog);
        
        this.xWindows = windows;
        this.xPixmaps = pixmaps;
        this.rendererFactory = rendererFactory;
    }

    @Override
    protected int[] getOpCodes() {
        return new int [] {
                OpCodes.CREATE_PIXMAP,
                OpCodes.FREE_PIXMAP
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
        case OpCodes.CREATE_PIXMAP: {
            final CreatePixmap createPixmap = log(messageLength, opcode, sequenceNumber, CreatePixmap.decode(stream));
            
            try {
                final XPixmap xPixmap = createPixmap(createPixmap, client);
                
                xPixmaps.addPixmap(createPixmap.getPid(), createPixmap.getDrawable(), xPixmap);
                
            } catch (IDChoiceException ex) {
                sendError(client, Errors.IDChoice, sequenceNumber, ex.getResource().getValue(), opcode);
            } catch (DrawableException ex) {
                sendError(client, Errors.Drawable, sequenceNumber, ex.getDrawable().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.FREE_PIXMAP: {
            final FreePixmap freePixmap = log(messageLength, opcode, sequenceNumber, FreePixmap.decode(stream));

            final DRAWABLE pixmapDrawable = freePixmap.getPixmap().toDrawable();
            
            final WindowsDisplayArea displayArea = findDisplayArea(xWindows, xPixmaps, pixmapDrawable);
            
            final XPixmap xPixmap = xPixmaps.removePixmap(freePixmap.getPixmap());
            
            if (xPixmap != null) {
                freePixmap(freePixmap, xPixmap, displayArea, client);
            }
            break;
        }
        }
        
    }

    private XPixmap createPixmap(CreatePixmap createPixmap, XClientOps client) throws IDChoiceException, DrawableException {
        
        final WindowsDisplayArea displayArea = findDisplayArea(xWindows, xPixmaps, createPixmap.getDrawable());
        
        client.checkAndAddResourceId(createPixmap.getPid());
        
        final Size size = new Size(
                createPixmap.getWidth().getValue(),
                createPixmap.getHeight().getValue());
        
        final PixelFormat pixelFormat = PixelFormat.RGB24;
        
        final OffscreenSurface surface = displayArea.allocateOffscreenSurface(
                size,
                pixelFormat);
        
        final XPixmap xPixmap = new XPixmap(
                getVisual(xWindows, xPixmaps, createPixmap.getDrawable()),
                surface,
                rendererFactory.createRenderer(surface, pixelFormat));
        
        return xPixmap;
    }
    

    private void freePixmap(FreePixmap freePixmap, XPixmap xPixmap, DisplayArea graphicsScreen, XClientOps client) {

        client.checkAndRemoveResourceId(freePixmap.getPixmap());
        
        if (xPixmap.getOffscreenSurface() != null) {
            graphicsScreen.freeOffscreenSurface(xPixmap.getOffscreenSurface());
        }
        
        xPixmap.dispose();
    }
}
