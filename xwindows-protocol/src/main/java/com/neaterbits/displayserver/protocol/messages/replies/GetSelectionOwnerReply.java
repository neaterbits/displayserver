package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class GetSelectionOwnerReply extends Reply {

    private final WINDOW owner;

    public GetSelectionOwnerReply(CARD16 sequenceNumber, WINDOW owner) {
        super(sequenceNumber);
    
        Objects.requireNonNull(owner);
        
        this.owner = owner;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        writeReplyHeader(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        stream.writeWINDOW(owner);
        
        stream.pad(20);
    }
}
