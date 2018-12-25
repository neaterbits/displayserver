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
    private final int bytesAfter;
    private final byte [] data;

    public GetPropertyReply(CARD16 sequenceNumber, CARD8 format, ATOM type,
            int bytesAfter, byte[] data) {
        
        super(sequenceNumber);
    
        this.format = format;
        this.type = type;
        this.bytesAfter = bytesAfter;
        this.data = data;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap(
                "format", format,
                "type", type,
                "bytesAfter", bytesAfter,
                "dataLength", data.length);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        stream.writeCARD8(format);

        writeSequenceNumber(stream);
        
        final int pad = XWindowsProtocolUtil.getPadding(data.length);
        
        stream.writeCARD32(new CARD32((data.length + pad) / 4));
        
        stream.writeATOM(type);

        stream.writeCARD32(new CARD32(bytesAfter));
        
        final int lengthValue = XWindowsProtocolUtil.getPropertyEncodeDataLength(format, data.length);

        stream.writeCARD32(new CARD32(lengthValue));
        
        stream.pad(12);
        
        stream.writeData(data);
        
        stream.pad(pad);
    }
}
