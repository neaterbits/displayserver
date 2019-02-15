package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.VISUALID;


public final class GetImageReply extends XReply {

    private final CARD8 depth;
    private final VISUALID visual;
    private final byte [] data;
    
    public static GetImageReply decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final CARD8 depth = stream.readCARD8();
        final CARD16 sequenceNumber = stream.readCARD16();
        
        final CARD32 replyLength = readReplyLength(stream);
        
        final VISUALID visual = stream.readVISUALID();
        
        stream.readPad(20);
        
        final byte [] data = stream.readData((int)(replyLength.getValue() * 4));
        
        return new GetImageReply(sequenceNumber, depth, visual, data);
    }
    
    public GetImageReply(CARD16 sequenceNumber, CARD8 depth, VISUALID visual, byte[] data) {
        super(sequenceNumber);

        Objects.requireNonNull(depth);
        Objects.requireNonNull(visual);
        Objects.requireNonNull(data);
        
        this.depth = depth;
        this.visual = visual;
        this.data = data;
    }

    public CARD8 getDepth() {
        return depth;
    }

    public VISUALID getVisual() {
        return visual;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "depth", depth,
                "visual", visual,
                "dataLength", data.length
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeCARD8(depth);
        
        writeSequenceNumber(stream);
        
        final int padding = XWindowsProtocolUtil.getPadding(data.length);
        
        stream.writeCARD32(new CARD32((data.length + padding) / 4));
    
        stream.writeVISUALID(visual);
        
        stream.pad(20);
        
        stream.writeData(data);
        
        stream.pad(padding);
    }
}
