package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.QueryFontReply;
import com.neaterbits.displayserver.protocol.types.FONTABLE;

public final class QueryFont extends Request {

    private final FONTABLE font;

    public static QueryFont decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new QueryFont(stream.readFONTABLE());
    }
    
    public QueryFont(FONTABLE font) {
        
        Objects.requireNonNull(font);
        
        this.font = font;
    }

    public FONTABLE getFont() {
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
        
        stream.writeFONTABLE(font);
    }

    @Override
    public int getOpCode() {
        return OpCodes.QUERY_FONT;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return QueryFontReply.class;
    }
}
