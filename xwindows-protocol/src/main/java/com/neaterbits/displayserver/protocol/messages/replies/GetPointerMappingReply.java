package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class GetPointerMappingReply extends XReply {

    private final CARD8 [] map;
    
    public GetPointerMappingReply(CARD16 sequenceNumber, CARD8[] map) {
        super(sequenceNumber);

        Objects.requireNonNull(map);
        
        this.map = map;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "map", Arrays.toString(map)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBYTE(new BYTE((byte)map.length));
        
        writeSequenceNumber(stream);
        
        final int padding = XWindowsProtocolUtil.getPadding(map.length);
        
        writeReplyLength(stream, (map.length + padding) / 4);
        
        stream.pad(24);
        
        encodeArray(map, stream);
        
        stream.pad(padding);
    }
}
