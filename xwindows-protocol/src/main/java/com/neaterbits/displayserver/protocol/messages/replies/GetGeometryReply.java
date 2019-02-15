package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class GetGeometryReply extends XReply {

    private final CARD8 depth;
    private final WINDOW root;
    
    private final INT16 x;
    private final INT16 y;
    
    private final CARD16 width;
    private final CARD16 height;
    
    private final CARD16 borderWidth;

    public GetGeometryReply(CARD16 sequenceNumber, CARD8 depth, WINDOW root, INT16 x, INT16 y, CARD16 width,
            CARD16 height, CARD16 borderWidth) {
        super(sequenceNumber);
        
        this.depth = depth;
        this.root = root;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.borderWidth = borderWidth;
    }

    public CARD8 getDepth() {
        return depth;
    }

    public WINDOW getRoot() {
        return root;
    }

    public INT16 getX() {
        return x;
    }

    public INT16 getY() {
        return y;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    public CARD16 getBorderWidth() {
        return borderWidth;
    }

    @Override
    protected final Object[] getServerToClientDebugParams() {
        return wrap(
                "depth", depth,
                "root", root,
                "x", x,
                "y", y,
                "width", width,
                "height", height,
                "borderWidth", borderWidth
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeCARD8(depth);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 0);
        
        stream.writeWINDOW(root);
        
        stream.writeINT16(x);
        stream.writeINT16(y);
        
        stream.writeCARD16(width);
        stream.writeCARD16(height);
        
        stream.writeCARD16(borderWidth);
        
        stream.pad(10);
    }
}
