package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class TranslateCoordinatesReply extends XReply {

    private final BOOL sameScreen;
    
    private final WINDOW child;
    
    private final INT16 dstX;
    private final INT16 dstY;
    
    public TranslateCoordinatesReply(CARD16 sequenceNumber, BOOL sameScreen, WINDOW child, INT16 dstX, INT16 dstY) {
        super(sequenceNumber);
    
        Objects.requireNonNull(sameScreen);
        Objects.requireNonNull(child);
        Objects.requireNonNull(dstX);
        Objects.requireNonNull(dstY);
        
        this.sameScreen = sameScreen;
        this.child = child;
        this.dstX = dstX;
        this.dstY = dstY;
    }

    public BOOL getSameScreen() {
        return sameScreen;
    }

    public WINDOW getChild() {
        return child;
    }

    public INT16 getDstX() {
        return dstX;
    }

    public INT16 getDstY() {
        return dstY;
    }
    
    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "sameScreen", sameScreen,
                "child", child,
                "dstX", dstX,
                "dstY", dstY
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBOOL(sameScreen);
        
        writeSequenceNumber(stream);

        writeReplyLength(stream, 0);
        
        stream.writeWINDOW(child);
        
        stream.writeINT16(dstX);
        stream.writeINT16(dstY);
        
        stream.pad(16);
    }
}
