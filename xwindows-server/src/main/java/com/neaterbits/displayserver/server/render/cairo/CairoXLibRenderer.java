package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.Buffer;
import com.neaterbits.displayserver.buffers.PixelConversion;
import com.neaterbits.displayserver.protocol.enums.CoordinateMode;
import com.neaterbits.displayserver.protocol.enums.ImageFormat;
import com.neaterbits.displayserver.protocol.enums.gc.Function;
import com.neaterbits.displayserver.protocol.enums.gc.LineStyle;
import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;
import com.neaterbits.displayserver.protocol.types.SEGMENT;
import com.neaterbits.displayserver.render.cairo.Cairo;
import com.neaterbits.displayserver.render.cairo.CairoFormat;
import com.neaterbits.displayserver.render.cairo.CairoImageSurface;
import com.neaterbits.displayserver.render.cairo.CairoOperator;
import com.neaterbits.displayserver.render.cairo.CairoSurface;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

final class CairoXLibRenderer implements XLibRenderer {

    private final CairoSurface surface;
    private final PixelConversion pixelConversion;

    private final Cairo cr;
    
    // private static int fileSequenceCounter = 0;
    
    CairoXLibRenderer(CairoSurface surface, PixelConversion pixelConversion) {
        
        Objects.requireNonNull(surface);
        Objects.requireNonNull(pixelConversion);
        
        this.surface = surface;
        this.pixelConversion = pixelConversion;
        
        this.cr = surface.createContext();
    }

    private void applyGC(XGC gc, boolean renderForeground) {
        
        final BYTE function = XLibRenderer.getGCValue(gc, XGCAttributes.FUNCTION, XGCAttributes::getFunction);
        
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

        final CARD32 planeMask = XLibRenderer.getGCValue(gc, XGCAttributes.PLANE_MASK, XGCAttributes::getPlaneMask);
        
        if (planeMask.getValue() != 0xFFFFFFFFL) {
            throw new UnsupportedOperationException();
        }
        
        final int sourceRGB;
        
        if (renderForeground) {
            sourceRGB = (int)XLibRenderer.getGCValue(gc, XGCAttributes.FOREGROUND, XGCAttributes::getForeground).getValue();
        }
        else {
            sourceRGB = (int)XLibRenderer.getGCValue(gc, XGCAttributes.BACKGROUND, XGCAttributes::getBackground).getValue();
        }

        cr.setSourceRGB(
                toCairoColor(pixelConversion.getRed(sourceRGB)),
                toCairoColor(pixelConversion.getGreen(sourceRGB)),
                toCairoColor(pixelConversion.getBlue(sourceRGB)));
        
        final int lineWidth = gc.getAttributes().getLineWidth().getValue();
        
        if (lineWidth == 0) {
            cr.setLineWidth(1);
        }
        else {
            cr.setLineWidth(lineWidth);
        }
        
        if (gc.getAttributes().getLineStyle().equals(LineStyle.OnOffDash)) {
         
            CARD8 [] dashes = gc.getDashes();
            
            if (dashes == null && gc.getAttributes().isSet(XGCAttributes.DASHES)) {
                
                final CARD8 dash = gc.getAttributes().getDashes();
                
                dashes = new CARD8 [] { dash, dash };
            }
            
            if (dashes != null) {
                
                final double [] cairoDashes = new double[dashes.length];
                
                for (int i = 0; i < dashes.length; ++ i) {
                    cairoDashes[i] = dashes[i].getValue();
                }
                
                cr.setDashes(
                        cairoDashes,
                        gc.getAttributes().getDashOffset().getValue()
                );
            }
        }
        else {
            cr.setDashes(new double[0], 0);
        }
        
        final RECTANGLE [] clipRectangles = gc.getClipRectangles();
        
        cr.resetClip();
        
        if (clipRectangles != null && clipRectangles.length > 0) {

            cr.newPath();

            for (RECTANGLE rectangle : clipRectangles) {

                cr.rectangle(
                        gc.getClipXOrigin().getValue() + rectangle.getX().getValue(),
                        gc.getClipYOrigin().getValue() + rectangle.getY().getValue(),
                        rectangle.getWidth().getValue(),
                        rectangle.getHeight().getValue());

            }
            
            cr.clip();
        }
    }
    
    private static double toCairoColor(int pixelComponent) {
        return ((double)pixelComponent) / 255.0;
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

    private void fillRectangle(int x, int y, int width, int height) {
        
        cr.rectangle(x, y, width, height);
        
        cr.strokePreserve();
        
        cr.fill();
    }

    
    
    @Override
    public void polyPoint(XGC gc, BYTE coordinateMode, POINT[] points) {

        if (points.length != 0) {

            applyGC(gc, true);
            
            cr.newPath();
            
            for (POINT point : points) {
                
                switch (coordinateMode.getValue()) {
                case CoordinateMode.ORIGIN:
                    cr.moveTo(point.getX(), point.getY());
                    cr.lineTo(point.getX(), point.getY());
                    break;
                    
                case CoordinateMode.PREVIOUS:
                    cr.relMoveTo(point.getX(), point.getY());
                    cr.relLineTo(0, 0);
                    break;
                    
                default:
                    throw new IllegalArgumentException();
                }
            }
            
            flush();
        }
    }

    @Override
    public void polyLine(XGC gc, BYTE coordinateMode, POINT[] points) {

        applyGC(gc, true);
        
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
    public void polySegment(XGC gc, SEGMENT[] segments) {

        if (segments.length != 0) {
            
            applyGC(gc, true);
            
            for (SEGMENT segment : segments) {
                
                cr.newPath();
                
                cr.moveTo(segment.getX1().getValue(), segment.getY1().getValue());
                cr.lineTo(segment.getX2().getValue(), segment.getY2().getValue());
            
                cr.stroke();
            }
        }
    }

    @Override
    public void polyFillRectangle(XGC gc, RECTANGLE[] rectangles) {

        if (rectangles.length != 0) {

            applyGC(gc, true);

            for (RECTANGLE rectangle : rectangles) {
                fillRectangle(
                        rectangle.getX().getValue(),
                        rectangle.getY().getValue(),
                        rectangle.getWidth().getValue(),
                        rectangle.getHeight().getValue());
            }
        }
    }
    

    @Override
    public void polyRectangle(XGC gc, RECTANGLE[] rectangles) {

        if (rectangles.length != 0) {
            
            applyGC(gc, true);
            
            for (RECTANGLE rectangle : rectangles) {
            
                cr.rectangle(
                        rectangle.getX().getValue(),
                        rectangle.getY().getValue(),
                        rectangle.getWidth().getValue(),
                        rectangle.getHeight().getValue());
            }
        }
    }

    @Override
    public void fillPoly(XGC gc, POINT[] points) {

        if (points.length != 0) {
            
            applyGC(gc, false);
            
            cr.newPath();
            
            for (POINT point : points) {
                
                cr.lineTo(point.getX(), point.getY());
            }
            
            cr.fill();
        }
    }

    @Override
    public void putImage(XGC gc, int format, int width, int height, int dstX, int dstY, int leftPad, int depth, byte[] data) {

        if (width != 0 && height != 0) {
            
            applyGC(gc, false);
            
            switch (format) {
            case ImageFormat.XYPIXMAP:
                
                if (depth != 1) {
                    throw new UnsupportedOperationException("TODO");
                }
                
                if (leftPad != 0) {
                    throw new UnsupportedOperationException("TODO");
                }
                
                break;
                
                
            case ImageFormat.ZPIXMAP:
                
                switch (depth) {
                
                case 1:
                    break;
                    
                case 24:
                    final CairoFormat cairoFormat = CairoFormat.RGB24;
                    
                    final int stride = cairoFormat.strideForWidth(width);
                    
                    if (data.length != stride * height) {
                        throw new IllegalArgumentException();
                    }
                    
                    final CairoImageSurface imageSurface = new CairoImageSurface(data, cairoFormat, width, height, stride);

                    // final int sequenceNumber = writeToPNG(imageSurface, "src");
                    
                    try {
                        cr.setSourceSurface(imageSurface, dstX, dstY);
                        
                        // cr.rectangle(dstX, dstY, width, height);
                        // cr.clip();

                        // cr.setSourceRGB(1.0, 1.0, 0.5);

                        // cr.paint();


                        cr.rectangle(dstX, dstY, width, height);
                        cr.fill();

                        surface.flush();

                        /*
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        */
                        
                        // writeToPNG(surface, "dst", sequenceNumber);
                    }
                    finally {
                        imageSurface.dispose();
                    }
                }
                break;
                
            }
        }
    }
    
    /*
    private int writeToPNG(CairoSurface surface, String suffix) {
        
        final int sequenceNumber = fileSequenceCounter ++;
     
        writeToPNG(surface, suffix, sequenceNumber);
    
        return sequenceNumber;
    }
        
    private void writeToPNG(CairoSurface surface, String suffix, int sequenceNumber) {
        final String fileName = System.getenv("HOME") + "/projects/displayserver/image" + sequenceNumber + "_" + suffix + ".png";
        
        final CairoStatus status = surface.writeToPNG(fileName);
        
        if (status != CairoStatus.SUCCESS) {
            throw new IllegalStateException("status=" + status);
        }
    }
    */

    @Override
    public void renderBitmap(XGC gc, Buffer buffer, int x, int y) {

        Objects.requireNonNull(gc);
        Objects.requireNonNull(buffer);
        
        applyGC(gc, false);
        
        final CairoFontBuffer fontBuffer = (CairoFontBuffer)buffer;
        
        final CairoSurface surface = fontBuffer.getSurface();

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
