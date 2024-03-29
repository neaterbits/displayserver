package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class QueryColorsReply extends XReply {

    private final RGB [] colors;

    public QueryColorsReply(CARD16 sequenceNumber, RGB[] colors) {
        super(sequenceNumber);
    
        Objects.requireNonNull(colors);
        
        this.colors = colors;
    }
    
    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "colors", outputArrayInBrackets(colors)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 2 * colors.length);
        
        stream.writeCARD16(new CARD16(colors.length));
        
        stream.pad(22);
        
        for (RGB rgb : colors) {
            rgb.encode(stream);
        }
    }
}
