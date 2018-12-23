package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.FONT;

public final class OpenFont extends Request {

    private final FONT fid;
    private final String name;

    public static OpenFont decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        readRequestLength(stream);
        
        final FONT fid = stream.readFONT();
        
        final CARD16 nameLength = stream.readCARD16();
        
        readUnusedCARD16(stream);
        
        final String name = stream.readSTRING8(nameLength.getValue());
        
        stream.readPad(XWindowsProtocolUtil.getPadding(nameLength.getValue()));
    
        return new OpenFont(fid, name);
    }
    
    public OpenFont(FONT fid, String name) {
        
        Objects.requireNonNull(fid);
        Objects.requireNonNull(name);
        
        this.fid = fid;
        this.name = name;
    }

    public FONT getFid() {
        return fid;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "fid", fid,
                "name", name
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        final int padding = XWindowsProtocolUtil.getPadding(name.length());
        
        writeRequestLength(stream, 3 + ((name.length() + padding) / 4));
        
        stream.writeFONT(fid);
        stream.writeCARD16(new CARD16(name.length()));
        
        writeUnusedCARD16(stream);
        
        stream.writeSTRING8(name);
        
        stream.pad(padding);
    }

    @Override
    public int getOpCode() {
        return OpCodes.OPEN_FONT;
    }
}
