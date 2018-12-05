package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;

public class GetPropertyReply extends Reply {

    private final CARD8 format;
    private final ATOM type;
    private final byte [] data;

    public GetPropertyReply(CARD16 sequenceNumber, CARD8 format, ATOM type,
            byte[] data) {
        
        super(sequenceNumber);
    
        this.format = format;
        this.type = type;
        this.data = data;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        stream.writeCARD8(format);

        writeSequenceNumber(stream);
        
        final int pad = XWindowsProtocolUtil.getPadding(data.length);
        
        stream.writeCARD32(new CARD32((data.length + pad) / 4));
        
        stream.writeATOM(type);

        stream.writeCARD32(new CARD32(data.length));
        
        final int lengthValue;
        
        switch (format.getValue()) {
        case 0:
            lengthValue = 0;
            break;
            
        case 8:
            lengthValue = data.length;
            break;
            
        case 16:
            lengthValue = data.length / 2;
            break;
            
        case 32:
            lengthValue = data.length / 4;
            break;
            
        
        default:
            throw new UnsupportedOperationException();
        }
        
        stream.writeCARD32(new CARD32(lengthValue));
        
        stream.pad(12);
        
        stream.writeData(data);
        
        stream.pad(pad);
    }
}
