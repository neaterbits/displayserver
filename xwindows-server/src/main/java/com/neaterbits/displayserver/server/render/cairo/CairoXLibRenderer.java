package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.Buffer;
import com.neaterbits.displayserver.buffers.PixelConversion;
import com.neaterbits.displayserver.protocol.enums.CoordinateMode;
import com.neaterbits.displayserver.protocol.enums.gc.Function;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.render.cairo.Cairo;
import com.neaterbits.displayserver.render.cairo.CairoImageSurface;
import com.neaterbits.displayserver.render.cairo.CairoOperator;
import com.neaterbits.displayserver.render.cairo.CairoSurface;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

final class CairoXLibRenderer implements XLibRenderer {

    private final CairoSurface surface;
    private final PixelConversion pixelConversion;

    private final Cairo cr;
    
    CairoXLibRenderer(CairoSurface surface, PixelConversion pixelConversion) {
        
        Objects.requireNonNull(surface);
        Objects.requireNonNull(pixelConversion);
        
        this.surface = surface;
        this.pixelConversion = pixelConversion;
        
        this.cr = new Cairo(surface);
    }

    private void applyGC(XGC gc) {
        
        final BYTE function = XLibRenderer.getGCValue(gc, GCAttributes.FUNCTION, GCAttributes::getFunction);
        
        final CairoOperator operator;
        
        switch (function.getValue()) {

        case Function.CLEAR:
            operator = CairoOperator.CLEAR;
            break;
            
        case Function.COPY:
            operator = CairoOperator.SOURCE;
            break;
            
        default:
            throw new UnsupportedOperationException();
        }

        cr.setOperator(operator);

        final CARD32 planeMask = XLibRenderer.getGCValue(gc, GCAttributes.PLANE_MASK, GCAttributes::getPlaneMask);
        
        if (planeMask.getValue() != 0xFFFFFFFFL) {
            throw new UnsupportedOperationException();
        }
        
        final int foreground = (int)XLibRenderer.getGCValue(gc, GCAttributes.FOREGROUND, GCAttributes::getForeground).getValue();
        
        cr.setSourceRGB(
                pixelConversion.getRed(foreground),
                pixelConversion.getGreen(foreground),
                pixelConversion.getBlue(foreground));
    }
    
    @Override
    public void flush() {
        surface.flush();
    }
    
    @Override
    public void fillRectangle(int x, int y, int width, int height, int r, int g, int b) {
        
        cr.setSourceRGB(r, g, b);
        
        cr.rectangle(x, y, width, height);
        
        cr.strokePreserve();
        
        cr.fill();
    }

    @Override
    public void polyLine(XGC gc, BYTE coordinateMode, POINT[] points) {

        applyGC(gc);
        
        if (points.length != 0) {

            cr.newPath();
            
            for (POINT point : points) {
                
                switch (coordinateMode.getValue()) {
                case CoordinateMode.ORIGIN:
                    cr.lineTo(point.getX(), point.getY());
                    break;
                    
                case CoordinateMode.PREVIOUS:
                    cr.relLineTo(point.getX(), point.getY());
                    break;
                    
                default:
                    throw new IllegalArgumentException();
                }
            }
            
            flush();
        }
    }

    @Override
    public void renderBitmap(XGC gc, Buffer buffer, int x, int y) {

        Objects.requireNonNull(gc);
        Objects.requireNonNull(buffer);
        
        applyGC(gc);
        
        final CairoFontBuffer fontBuffer = (CairoFontBuffer)buffer;
        
        final CairoImageSurface surface = fontBuffer.getSurface();

  //      cr.rectangle(x, y, surface.getWidth(), surface.getHeight());
        //cr.clip();

        cr.newPath();
//        cr.setSourceSurface(surface, 0, 0);
        
        // cr.moveTo(x, y);

        cr.setSourceRGB(0, 0, 0);

        cr.maskSurface(surface, x, y);


        /*
        cr.rectangle(x, y, surface.getWidth(), surface.getHeight());
        cr.strokePreserve();
        cr.fill();
        */

        
        // cr.paint();
    }

    @Override
    public void dispose() {
        cr.dispose();
        surface.dispose();
    }
}
