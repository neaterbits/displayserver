package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.INT16;

public final class GetImage extends XRequest {

    private final BYTE format;
    
    private final DRAWABLE drawable;
    
    private final INT16 x;
    private final INT16 y;
    
    private final CARD16 width;
    private final CARD16 height;
    
    private final CARD32 planeMask;

    public static GetImage decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE format = stream.readBYTE();
        
        readRequestLength(stream);
        
        return new GetImage(
                format,
                stream.readDRAWABLE(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD32());
    }
    
    public GetImage(BYTE format, DRAWABLE drawable, INT16 x, INT16 y, CARD16 width, CARD16 height, CARD32 planeMask) {

        Objects.requireNonNull(format);
        Objects.requireNonNull(drawable);
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        Objects.requireNonNull(width);
        Objects.requireNonNull(height);
        Objects.requireNonNull(planeMask);
        
        this.format = format;
        this.drawable = drawable;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.planeMask = planeMask;
    }


    public BYTE getFormat() {
        return format;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }

    public INT16 getX() {
        return x;
    }

    public INT16 getY() {
        return y;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    public CARD32 getPlaneMask() {
        return planeMask;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "format", format,
                "drawable", drawable,
                "x", x,
                "y", y,
                "width", width,
                "height", height,
                "planeMask", planeMask
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBYTE(format);
        
        writeRequestLength(stream, 5);
        
        stream.writeDRAWABLE(drawable);
        
        stream.writeINT16(x);
        stream.writeINT16(y);
        
        stream.writeCARD16(width);
        stream.writeCARD16(height);
        
        stream.writeCARD32(planeMask);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GET_IMAGE;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return GetImageReply.class;
    }
}
