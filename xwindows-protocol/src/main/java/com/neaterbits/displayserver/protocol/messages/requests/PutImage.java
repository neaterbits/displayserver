package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;

public final class PutImage extends Request {

    private final BYTE format;
    private final DRAWABLE drawable;
    private final GCONTEXT gc;
    
    private final CARD16 width;
    private final CARD16 height;
    
    private final INT16 dstX;
    private final INT16 dstY;
    
    private final CARD8 leftPad;
    private final CARD8 depth;
    
    private final byte [] data;
    private final int dataOffset;
    private final int dataLength;

    public static PutImage decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE format = stream.readBYTE();
        
        final CARD16 requestLength = stream.readCARD16();
        
        final DRAWABLE drawable = stream.readDRAWABLE();
        final GCONTEXT gc = stream.readGCONTEXT();
        final CARD16 width = stream.readCARD16();
        final CARD16 height = stream.readCARD16();
        final INT16 dstX = stream.readINT16();
        final INT16 dstY = stream.readINT16();
        final CARD8 leftPad = stream.readCARD8();
        final CARD8 depth = stream.readCARD8();
     
        readUnusedCARD16(stream);
        
        final int dataLength = (requestLength.getValue() - 6) * 4;
        
        System.out.println("## requestLength: " + requestLength);
        System.out.println("## dataLength: " + dataLength);
        
        final byte [] data = stream.readData(dataLength);
        
        return new PutImage(format, drawable, gc, width, height, dstX, dstY, leftPad, depth, data, 0, data.length);
    }

    public PutImage(BYTE format, DRAWABLE drawable, GCONTEXT gc, CARD16 width, CARD16 height, INT16 dstX, INT16 dstY,
            CARD8 leftPad, CARD8 depth, byte[] data, int dataOffset, int dataLength) {
        
        this.format = format;
        this.drawable = drawable;
        this.gc = gc;
        this.width = width;
        this.height = height;
        this.dstX = dstX;
        this.dstY = dstY;
        this.leftPad = leftPad;
        this.depth = depth;
        this.data = data;
        this.dataOffset = dataOffset;
        this.dataLength = dataLength;
    }

    public BYTE getFormat() {
        return format;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }

    public GCONTEXT getGC() {
        return gc;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    public INT16 getDstX() {
        return dstX;
    }

    public INT16 getDstY() {
        return dstY;
    }

    public CARD8 getLeftPad() {
        return leftPad;
    }

    public CARD8 getDepth() {
        return depth;
    }

    public byte[] getData() {
        return data;
    }
    
    public int getDataOffset() {
        return dataOffset;
    }

    public int getDataLength() {
        return dataLength;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "format", format,
                "drawable", drawable,
                "gc", gc,
                "width", width,
                "height", height,
                "dstX", dstX,
                "dstY", dstY,
                "leftPad", leftPad,
                "depth", depth,
                "data", data,
                "dataOffset", dataOffset,
                "dataLength", dataLength
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBYTE(format);
        
        final int pad = XWindowsProtocolUtil.getPadding(this.dataLength);
        final int dataLength = 6 + (this.dataLength + pad) / 4;
        
        if (dataLength > (2 << 16)) {
            throw new IllegalArgumentException();
        }
        
        stream.writeCARD16(new CARD16(dataLength));
        
        stream.writeDRAWABLE(drawable);
        stream.writeGCONTEXT(gc);
        stream.writeCARD16(width);
        stream.writeCARD16(height);
        stream.writeINT16(dstX);
        stream.writeINT16(dstY);
        stream.writeCARD8(leftPad);
        stream.writeCARD8(depth);
        
        writeUnusedCARD16(stream);
        
        stream.writeData(data, dataOffset, this.dataLength);
        
        stream.pad(pad);
    }

    @Override
    public int getOpCode() {
        return OpCodes.PUT_IMAGE;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
