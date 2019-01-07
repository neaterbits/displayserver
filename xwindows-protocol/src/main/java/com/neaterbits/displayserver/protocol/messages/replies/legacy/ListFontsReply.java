package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class ListFontsReply extends Reply {

    private final String [] names;

    public ListFontsReply(CARD16 sequenceNumber, String[] names) {
        super(sequenceNumber);
    
        Objects.requireNonNull(names);
        
        this.names = names;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("names", Arrays.toString(names));
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        int replyLength = Arrays.stream(names)
                .map(name -> name.length() + 1)
                .reduce(Integer::sum)
                .orElse(0);

        writeReplyHeader(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        final int padding = XWindowsProtocolUtil.getPadding(replyLength);
        
        writeReplyLength(stream, (replyLength + padding) / 4);
        
        stream.writeCARD16(new CARD16(names.length));
    
        stream.pad(22);
        
        writeStrings(stream, names);
        
        stream.pad(padding);
    }
}
