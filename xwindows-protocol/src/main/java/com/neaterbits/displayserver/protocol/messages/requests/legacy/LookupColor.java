package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.LookupColorReply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class LookupColor extends Request {

    private final COLORMAP cmap;
    private final String name;

    public static LookupColor decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        final COLORMAP cmap = stream.readCOLORMAP();
        
        final CARD16 nameLength = stream.readCARD16();
        
        readUnusedCARD16(stream);
        
        final String name = stream.readSTRING8(nameLength.getValue());
        
        stream.readPad(XWindowsProtocolUtil.getPadding(nameLength.getValue()));
        
        return new LookupColor(cmap, name);
    }
    
    public LookupColor(COLORMAP cmap, String name) {
        
        Objects.requireNonNull(cmap);
        Objects.requireNonNull(name);
        
        this.cmap = cmap;
        this.name = name;
    }
    
    public COLORMAP getCmap() {
        return cmap;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "cmap", cmap,
                "name", name
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        final int padding = XWindowsProtocolUtil.getPadding(name.length());
        
        writeRequestLength(stream, 3 + ((name.length() + padding) / 4));
        
        stream.writeCOLORMAP(cmap);
        
        stream.writeCARD16(new CARD16(name.length()));
        
        writeUnusedCARD16(stream);
        
        stream.writeSTRING8(name);
        
        stream.pad(padding);
    }

    @Override
    public int getOpCode() {
        return OpCodes.LOOKUP_COLOR;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return LookupColorReply.class;
    }
}
