package com.neaterbits.displayserver.framebuffer.xwindows;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.GetImageListener;
import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.driver.xwindows.common.ReplyListener;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.protocol.enums.ImageFormat;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetImage;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;

abstract class XWindowsBaseBuffer implements BufferOperations {

    final XWindowsDriverConnection driverConnection;
    final GCONTEXT gc;

    abstract DRAWABLE getDrawable();
    
    abstract int getDepth();
    
    
    XWindowsBaseBuffer(XWindowsDriverConnection driverConnection, GCONTEXT gc) {
        
        Objects.requireNonNull(driverConnection);
        Objects.requireNonNull(gc);
        
        this.driverConnection = driverConnection;
        this.gc = gc;
    }

    final XWindowsDriverConnection getDriverConnection() {
        return driverConnection;
    }

    @Override
    public final void putImage(int x, int y, int width, int height, PixelFormat format, byte[] data) {
        
        if (format.getDepth() != getDepth()) {
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
                    new BYTE((byte)2),
                    getDrawable(), gc,
                    new CARD16(width), new CARD16(h),
                    new INT16((short)dstX), new INT16((short)dstY),
                    new CARD8((short)0),
                    new CARD8((short)getDepth()),
                    data,
                    dataOffset,
                    dataLength);
            
            driverConnection.sendRequest(putImage);
            
            scanlinesLeftToWrite -= h;
        }
    }

    @Override
    public final void getImage(int x, int y, int width, int height, PixelFormat format, GetImageListener listener) {

        final GetImage getImageRequest = new GetImage(
                ImageFormat.ZPixMap,
                getDrawable(),
                new INT16((short)x), new INT16((short)y),
                new CARD16(width), new CARD16(height),
                new CARD32(0x00FFFFFFL));
        
        driverConnection.sendRequestWaitReply(getImageRequest, new ReplyListener() {
            
            @Override
            public void onReply(Reply reply) {
                final GetImageReply getImageReply = (GetImageReply)reply;
                
                listener.onResult(getImageReply.getData());
            }
            
            @Override
            public void onError(Error error) {
                listener.onError();
            }
        });
    }
}
