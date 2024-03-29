package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class FreePixmap extends XRequest {

    private final PIXMAP pixmap;

    public FreePixmap(PIXMAP pixmap) {
        this.pixmap = pixmap;
    }

    public static FreePixmap decode(XWindowsProtocolInputStream stream) throws IOException {
        stream.readBYTE();
        stream.readCARD16();
        
        return new FreePixmap(stream.readPIXMAP());
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("pixmap", pixmap);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 2);
        
        stream.writePIXMAP(pixmap);
    }
    
    @Override
    public int getOpCode() {
        return OpCodes.FREE_PIXMAP;
    }

    public PIXMAP getPixmap() {
        return pixmap;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
