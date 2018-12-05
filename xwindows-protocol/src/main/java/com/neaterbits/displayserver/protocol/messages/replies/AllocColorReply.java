package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;

public final class AllocColorReply extends Reply {

    private final CARD16 red;
    private final CARD16 green;
    private final CARD16 blue;
    
    private final CARD32 pixel;

    public AllocColorReply(CARD16 sequenceNumber, CARD16 red, CARD16 green, CARD16 blue, CARD32 pixel) {
        super(sequenceNumber);

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.pixel = pixel;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBYTE(new BYTE((byte)0));
        
        writeSequenceNumber(stream);
        
        stream.writeCARD32(new CARD32(0));
        
        stream.writeCARD16(red);
        stream.writeCARD16(green);
        stream.writeCARD16(blue);
        
        stream.writeCARD16(new CARD16(0));
        
        stream.writeCARD32(pixel);
        
        stream.pad(12);
    }
}
