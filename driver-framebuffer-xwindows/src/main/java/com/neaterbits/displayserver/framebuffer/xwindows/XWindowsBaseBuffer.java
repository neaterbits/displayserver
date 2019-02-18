package com.neaterbits.displayserver.framebuffer.xwindows;

import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.GetImageListener;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.driver.xwindows.common.ReplyListener;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.protocol.enums.ImageFormat;
import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.DEPTH;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.FORMAT;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.VISUALTYPE;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.messages.requests.CopyArea;
import com.neaterbits.displayserver.protocol.messages.requests.GetImage;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.PolyFillRectangle;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.RECTANGLE;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.render.cairo.CairoSurface;
import com.neaterbits.displayserver.render.cairo.xcb.CairoXCBSurface;
import com.neaterbits.displayserver.render.cairo.xcb.DrawableType;
import com.neaterbits.displayserver.types.Size;

abstract class XWindowsBaseBuffer implements BufferOperations {

    final XWindowsDriverConnection driverConnection;
    final GCONTEXT gc;
    final Size size;
    final int depth;

    abstract DRAWABLE getDrawable();
    
    abstract DrawableType getDrawableType();
    
    abstract int getScreenNo();
    
    XWindowsBaseBuffer(XWindowsDriverConnection driverConnection, GCONTEXT gc, Size size, int depth) {
        
        Objects.requireNonNull(driverConnection);
        Objects.requireNonNull(gc);
        Objects.requireNonNull(size);
        
        this.driverConnection = driverConnection;
        this.gc = gc;
        
        this.size = size;
        this.depth = depth;
    }

    final XWindowsDriverConnection getDriverConnection() {
        return driverConnection;
    }

    @Override
    public final Size getSize() {
        return size;
    }

    @Override
    public final int getDepth() {
        return depth;
    }

    @Override
    public final void putImage(int x, int y, int width, int height, PixelFormat format, byte[] data) {
        
        if (format.getDepth() != depth) {
            throw new IllegalArgumentException();
        }
        
        int scanlinesLeftToWrite = height;
        
        final int dstX = x;
        
        int dstY = y;
        
        int dataOffset = 0;
        
        final int maxBytes = (65536 - 6) * 4;
        
        final int stride = width * format.getBytesPerPixel();
        
        if (stride > maxBytes) {
            throw new UnsupportedOperationException();
        }

        final int numLines = maxBytes % stride;
        
        while (scanlinesLeftToWrite > 0) {
            
            final int h = scanlinesLeftToWrite > numLines
                    ? numLines
                    : scanlinesLeftToWrite;
            
            final int dataLength = h * stride;
            
            final PutImage putImage = new PutImage(
                    ImageFormat.ZPixMap,
                    getDrawable(), gc,
                    new CARD16(width), new CARD16(h),
                    new INT16((short)dstX), new INT16((short)dstY),
                    new CARD8((short)0),
                    new CARD8((short)depth),
                    data,
                    dataOffset,
                    dataLength);
            
            driverConnection.sendRequest(putImage);
            
            scanlinesLeftToWrite -= h;
        }
    }

    private FORMAT getFormat(int depth) {
        
        final ServerMessage serverMessage = driverConnection.getServerMessage();
        
        for (FORMAT format : serverMessage.getPixmapFormats()) {
            if (format.getDepth().getValue() == depth) {
                return format;
            }
        }
        
        throw new IllegalStateException("No format of depth " + depth + " in " + Arrays.toString(serverMessage.getPixmapFormats()));
    }
    
    private VISUALTYPE getVisual(ServerMessage serverMessage, VISUALID visual) {
        
        final SCREEN [] screens = serverMessage.getScreens();
        
        for (int i = 0; i < screens.length; ++ i) {
            final SCREEN screen = screens[i];

            for (DEPTH depth : screen.getAllowedDepths()) {
                for (VISUALTYPE visualType : depth.getVisuals()) {
                    if (visualType.getVisualId().equals(visual)) {
                        return visualType;
                    }
                }
            }
        }

        throw new IllegalStateException("No visual for " + visual);
    }
           
    private VISUALTYPE getVisual(int screenNo, int pixelDepth, VISUALID visual) {
        
        final ServerMessage serverMessage = driverConnection.getServerMessage();
        return getVisual(serverMessage, visual);

        /*
        
        final SCREEN [] screens = serverMessage.getScreens();
        
        final SCREEN screen = screens[screenNo];
        
        for (DEPTH depth : screen.getAllowedDepths()) {
            if (depth.getDepth().getValue() == pixelDepth) {
                
                for (VISUALTYPE visualtype : depth.getVisuals()) {
                    if (visualtype.getVisualId().equals(visual)) {
                        return visualtype;
                    }
                }
            }
        }
        
        throw new IllegalStateException("No visual for " + visual);
         */
    }
    
    private static boolean isPixelFormat(VISUALTYPE visualType, PixelFormat pixelFormat) {
        return   
                  visualType.getBitsPerRGBValue().getValue() == pixelFormat.getBitsPerColorComponent()
               && visualType.getRedMask().getValue()    == pixelFormat.getRedMask()
               && visualType.getGreenMask().getValue()  == pixelFormat.getGreenMask()
               && visualType.getBlueMask().getValue()   == pixelFormat.getBlueMask();
               
    }

    private static PixelFormat getPixelFormat(FORMAT format, VISUALTYPE visualType) {
        
        final PixelFormat pixelFormat;
        
        switch (format.getDepth().getValue()) {
        
        case 24:
            switch (format.getBitsPerPixel().getValue()) {
            case 24:
                if (isPixelFormat(visualType, PixelFormat.RGB24)) {
                    pixelFormat = PixelFormat.RGB24;
                }
                else {
                    throw new UnsupportedOperationException();
                }
                break;
                
            case 32:
                if (isPixelFormat(visualType, PixelFormat.RGBA32)) {
                    pixelFormat = PixelFormat.RGBA32;
                }
                else if (isPixelFormat(visualType, PixelFormat.ARGB32)) {
                    pixelFormat = PixelFormat.RGB32;
                }
                else {
                    throw new UnsupportedOperationException("Unknown pixel format " + visualType);
                }
                break;
                
            default:
                throw new UnsupportedOperationException();
            }
            
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return pixelFormat;
    }

    private static PixelFormat getPixelFormat(FORMAT format) {
        
        final PixelFormat pixelFormat;
        
        switch (format.getDepth().getValue()) {
        
        case 24:
            switch (format.getBitsPerPixel().getValue()) {
            case 24:
                pixelFormat = PixelFormat.RGB24;
                break;
                
            case 32:
                pixelFormat = PixelFormat.RGB32;
                break;
                
            default:
                throw new UnsupportedOperationException();
            }
            
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return pixelFormat;
    }

    @Override
    public final void getImage(int x, int y, int width, int height, PixelFormat pixelFormat, GetImageListener listener) {

        final GetImage getImageRequest = new GetImage(
                ImageFormat.ZPixMap,
                getDrawable(),
                new INT16((short)x), new INT16((short)y),
                new CARD16(width), new CARD16(height),
                new CARD32(0xFFFFFFFFL));
        
        driverConnection.sendRequestWaitReply(getImageRequest, new ReplyListener() {
            
            @Override
            public void onReply(XReply reply) {
                final GetImageReply getImageReply = (GetImageReply)reply;
                
                final int returnedDepth = getImageReply.getDepth().getValue();
                
                final FORMAT format = getFormat(returnedDepth);
                
                final PixelFormat returnedPixelFormat;
                
                if (!getImageReply.getVisual().equals(VISUALID.None)) {
                    final VISUALTYPE visualType = getVisual(getScreenNo(), returnedDepth, getImageReply.getVisual());
                
                    returnedPixelFormat = getPixelFormat(format, visualType);
                }
                else {
                    returnedPixelFormat = getPixelFormat(format);
                }
                
                byte [] data;
                
                if (returnedPixelFormat.equals(pixelFormat)) {
                    data = getImageReply.getData();
                }
                else {
                    data = convertPixelData(
                            getImageReply.getData(),
                            getImageRequest.getWidth().getValue(), getImageRequest.getHeight().getValue(),
                            returnedPixelFormat,
                            pixelFormat);
                }

                listener.onResult(data);
            }
            
            @Override
            public void onError(XError error) {
                listener.onError();
            }
        });
    }
    
    @Override
    public final CairoSurface createCairoSurface() {

        final DRAWABLE drawable = getDrawable();
        
        return CairoXCBSurface.create(
                driverConnection.getXCBConnection(),
                drawable.getValue(),
                getDrawableType(),
                driverConnection.getXCBVisual(),
                size.getWidth(),
                size.getHeight());
    }

    @Override
    public void copyArea(BufferOperations src, int srcX, int srcY, int dstX, int dstY, int width, int height) {

        final DRAWABLE srcDrawable = ((XWindowsBaseBuffer)src).getDrawable();
        final DRAWABLE dstDrawable = getDrawable();
        
        driverConnection.sendRequest(
                new CopyArea(
                        srcDrawable, dstDrawable,
                        gc,
                        new INT16((short)srcX), new INT16((short)srcY),
                        new INT16((short)dstX), new INT16((short)dstY),
                        new CARD16(width), new CARD16(height)));
    }

    
    @Override
    public void flush() {
        driverConnection.flush();
    }

    @Override
    public void writeTestImage(int x, int y, int width, int height) {
        
        final DRAWABLE dstDrawable = getDrawable();

        driverConnection.sendRequest(new PolyFillRectangle(dstDrawable, gc, new RECTANGLE[] {
                new RECTANGLE(
                        new INT16((short)x),
                        new INT16((short)y),
                        new CARD16(width),
                        new CARD16(height))
        }));
    }

    private static int getPixel(byte [] data, int index, PixelFormat format) {
        
        int pixel = 0;
        
        for (int i = 0; i < format.getBytesPerPixel(); ++ i) {
            
            pixel <<= 8;
            pixel |= data[index + i];
        }
        
        return pixel;
    }

    private static void putPixel(byte [] data, int index, PixelFormat format, int pixel) {
        
       for (int i = 0; i < format.getBytesPerPixel(); ++ i) {
            
            pixel >>>= 8;
            data[index + i] = (byte)(pixel & 0x000000FF);
        }
    }
    
    private static byte [] convertPixelData(
            byte [] pixelData,
            int width, int height,
            PixelFormat fromFormat,
            PixelFormat toFormat) {
        
        final int numPixels = width * height;
        final int numBytes = toFormat.getBytesPerPixel() * numPixels;
        
        System.out.println("## getting buffer for " + numPixels + "/" + toFormat.getBytesPerPixel() + ": " + numBytes);
        
        final byte [] toData = new byte[numBytes];
        
        int srcIdx = 0;
        int dstIdx = 0;
        
        for (int i = 0; i < numPixels; ++ i) {
            
            final int srcPixel = getPixel(pixelData, srcIdx, fromFormat);
            
            final int red   = fromFormat.getRed(srcPixel);
            final int green = fromFormat.getGreen(srcPixel);
            final int blue  = fromFormat.getBlue(srcPixel);
        
            final int dstPixel =   (red   << toFormat.getRedShift())
                                 | (green << toFormat.getGreenShift())
                                 | (blue  << toFormat.getBlueShift());
            
            
            putPixel(toData, dstIdx, toFormat, dstPixel);

            srcIdx += fromFormat.getBytesPerPixel();
            dstIdx += toFormat.getBytesPerPixel();
        }
        
        return toData;
    }
}
