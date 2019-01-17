package com.neaterbits.displayserver.xwindows.core.processing;

import java.util.Objects;
import java.util.function.Supplier;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.xwindows.model.XPixmap;
import com.neaterbits.displayserver.xwindows.model.XPixmapsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

abstract class BaseXCorePixmapRenderProcessor extends XOpCodeProcessor {

    private final XPixmapsConstAccess xPixmaps;
    
    protected BaseXCorePixmapRenderProcessor(XWindowsServerProtocolLog protocolLog, XPixmapsConstAccess xPixmaps) {
        super(protocolLog);
        
        Objects.requireNonNull(xPixmaps);
        
        this.xPixmaps = xPixmaps;
    }

    final void renderWindowBackground(XWindow xWindow) {

        renderWindowBackground(xWindow, 0, 0, xWindow.getWidth(), xWindow.getHeight());
    }

    final void renderWindowBackground(XWindow xWindow, int x, int y, int width, int height) {

        renderWindowBackground(
                xWindow.getCurrentWindowAttributes(),
                xWindow.getRenderer(),
                xWindow.getSurface(),
                // Pass as lambda function to avoid triggering mock invocations in unit tests
                xWindow.getWindow()::getPixelFormat,
                x, y,
                width, height);
    }
    
    private void renderWindowBackground(
            XWindowAttributes windowAttributes,
            XLibRenderer renderer,
            BufferOperations windowBuffer,
            Supplier<PixelFormat> pixelFormatSupplier,
            int x, int y,
            int width, int height) {
        
        if (windowAttributes.isSet(XWindowAttributes.BACKGROUND_PIXEL)) {
            
            final int bgPixel = (int)windowAttributes.getBackgroundPixel().getValue();
            
            final PixelFormat pixelFormat = pixelFormatSupplier.get();
            
            renderer.fillRectangle(
                    x, y,
                    width, height,
                    pixelFormat.getRed(bgPixel),
                    pixelFormat.getGreen(bgPixel),
                    pixelFormat.getBlue(bgPixel));
            
            renderer.flush();
        }
        else if (    windowAttributes.isSet(XWindowAttributes.BACKGROUND_PIXMAP)
            && !windowAttributes.getBackgroundPixmap().equals(PIXMAP.None)) {

            final PIXMAP pixmapResource = windowAttributes.getBackgroundPixmap();

            final XPixmap xPixmap = xPixmaps.getPixmap(pixmapResource);

            if (xPixmap != null) {

                final OffscreenSurface src = xPixmap.getOffscreenSurface();



                final int srcWidth = src.getWidth();
                final int srcHeight = src.getHeight();
                
                if (srcWidth == 0 || srcHeight == 0) {
                    throw new IllegalStateException();
                }
                
                for (int dstY = 0; dstY < height;) {

                    final int tileHeight = Math.min(srcHeight, height - dstY);

                    for (int dstX = 0; dstX < width;) {

                        final int tileWidth = Math.min(srcWidth, width - dstX);

                        windowBuffer.copyArea(src, 0, 0, dstX + x, dstY + y, tileWidth, tileHeight);

                        dstX += tileWidth;
                    }

                    dstY += tileHeight;
                }
            }
        }
    }
}
