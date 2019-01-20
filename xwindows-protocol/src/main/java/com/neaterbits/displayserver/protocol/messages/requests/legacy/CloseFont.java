package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.FONT;

public final class CloseFont extends XRequest {
    private final FONT font;

    public static CloseFont decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);

        readRequestLength(stream);

        return new CloseFont(stream.readFONT());
    }
    
    public CloseFont(FONT font) {
        
        Objects.requireNonNull(font);

        this.font = font;
    }

    public FONT getFont() {
        return font;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "font", font
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeOpCode(stream);
        
        writeUnusedByte(stream);

        writeRequestLength(stream, 2);

        stream.writeFONT(font);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CLOSE_FONT;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
