package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;

public final class CopyArea extends Request {
    
    private final DRAWABLE srcDrawable;
    private final DRAWABLE dstDrawable;
    private final GCONTEXT gc;
    
    private final INT16 srcX;
    private final INT16 srcY;
    
    private final INT16 dstX;
    private final INT16 dstY;
    
    private final CARD16 width;
    private final CARD16 height;
    
    public static CopyArea decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        readRequestLength(stream);
        
        return new CopyArea(
                stream.readDRAWABLE(),
                stream.readDRAWABLE(),
                stream.readGCONTEXT(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readCARD16(),
                stream.readCARD16());
        
    }
    

    public CopyArea(
            DRAWABLE srcDrawable, DRAWABLE dstDrawable,
            GCONTEXT gc,
            INT16 srcX, INT16 srcY,
            INT16 dstX, INT16 dstY,
            CARD16 width, CARD16 height) {

        Objects.requireNonNull(srcDrawable);
        Objects.requireNonNull(dstDrawable);
        
        Objects.requireNonNull(srcX);
        Objects.requireNonNull(srcY);
        
        Objects.requireNonNull(dstX);
        Objects.requireNonNull(dstY);
        
        Objects.requireNonNull(width);
        Objects.requireNonNull(height);
        
        this.srcDrawable = srcDrawable;
        this.dstDrawable = dstDrawable;
        this.gc = gc;
        this.srcX = srcX;
        this.srcY = srcY;
        this.dstX = dstX;
        this.dstY = dstY;
        this.width = width;
        this.height = height;
    }
    
    public DRAWABLE getSrcDrawable() {
        return srcDrawable;
    }

    public DRAWABLE getDstDrawable() {
        return dstDrawable;
    }

    public GCONTEXT getGC() {
        return gc;
    }

    public INT16 getSrcX() {
        return srcX;
    }

    public INT16 getSrcY() {
        return srcY;
    }

    public INT16 getDstX() {
        return dstX;
    }

    public INT16 getDstY() {
        return dstY;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    @Override
    public Object[] getDebugParams() {
        
        return wrap(
                "srcDrawable", srcDrawable,
                "dstDrawable", dstDrawable,
                "gc", gc,
                "srcX", srcX,
                "srcY", srcY,
                "dstX", dstX,
                "dstY", dstY,
                "width", width,
                "height", height
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 7);
        
        stream.writeDRAWABLE(srcDrawable);
        stream.writeDRAWABLE(dstDrawable);
        stream.writeGCONTEXT(gc);
        stream.writeINT16(srcX);
        stream.writeINT16(srcY);
        stream.writeINT16(dstX);
        stream.writeINT16(dstY);
        stream.writeCARD16(width);
        stream.writeCARD16(height);
    }

    @Override
    public int getOpCode() {
        return OpCodes.COPY_AREA;
    }


    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
