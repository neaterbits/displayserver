package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.GetGeometryReply;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;

public final class GetGeometry extends XRequest {

    private final DRAWABLE drawable;

    
    public static GetGeometry decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);

        return new GetGeometry(stream.readDRAWABLE());
    }
    
    public GetGeometry(DRAWABLE drawable) {
        
        Objects.requireNonNull(drawable);
        
        this.drawable = drawable;
    }

    public DRAWABLE getDrawable() {
        return drawable;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "drawable", drawable
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
        
        stream.writeDRAWABLE(drawable);
    }

    @Override
    public int getOpCode() {
        return OpCodes.GET_GEOMETRY;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return GetGeometryReply.class;
    }
}
