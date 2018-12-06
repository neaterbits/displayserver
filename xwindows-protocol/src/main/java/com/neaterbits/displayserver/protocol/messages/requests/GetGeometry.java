package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.OpCodes;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;

public final class GetGeometry extends Request {

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
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream, OpCodes.GET_GEOMETRY);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
        
        stream.writeDRAWABLE(drawable);
    }
}
