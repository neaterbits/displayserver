package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.AllocNamedColorReply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.COLORMAP;

public final class AllocNamedColor extends Request {

    private final COLORMAP cmap;
    private final String name;

    public static final AllocNamedColor decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        final COLORMAP cmap = stream.readCOLORMAP();
        
        final CARD16 length = stream.readCARD16();
        
        final int nameLength = length.getValue();
        
        final int padding = XWindowsProtocolUtil.getPadding(nameLength);
        
        readUnusedCARD16(stream);
        
        final String name = stream.readSTRING8(nameLength);
        
        stream.readPad(padding);

        return new AllocNamedColor(cmap, name);
    }
    
    public AllocNamedColor(COLORMAP cmap, String name) {
    
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
        
        writeRequestLength(stream, 3 + (name.length() + padding) / 4);
        
        stream.writeCOLORMAP(cmap);
        
        stream.writeCARD16(new CARD16(name.length()));
        
        stream.pad(2);
        
        stream.writeSTRING8(name);
        
        stream.pad(padding);
    }

    @Override
    public int getOpCode() {
        return OpCodes.ALLOC_NAMED_COLOR;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return AllocNamedColorReply.class;
    }
}
