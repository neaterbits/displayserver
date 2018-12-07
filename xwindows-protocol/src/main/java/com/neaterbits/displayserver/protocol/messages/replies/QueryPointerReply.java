package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class QueryPointerReply extends Reply {

    private final BOOL sameScreen;

    private final WINDOW root;
    private final WINDOW child;
    
    private final INT16 rootX;
    private final INT16 rootY;
    
    private final INT16 winX;
    private final INT16 winY;
    
    private final SETofKEYBUTMASK mask;
    
    public static QueryPointerReply decode(XWindowsProtocolInputStream stream) throws IOException {

        final BOOL sameScreen = stream.readBOOL();
        
        readUnusedByte(stream);
        
        final CARD16 sequenceNumber = stream.readCARD16();
        
        readReplyLength(stream);
        
        return new QueryPointerReply(
                sequenceNumber,
                sameScreen,
                stream.readWINDOW(),
                stream.readWINDOW(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readSETofKEYBUTMASK());
    }

    public QueryPointerReply(CARD16 sequenceNumber, BOOL sameScreen, WINDOW root, WINDOW child, INT16 rootX,
            INT16 rootY, INT16 winX, INT16 winY, SETofKEYBUTMASK mask) {
        
        super(sequenceNumber);
        
        Objects.requireNonNull(sameScreen);
        Objects.requireNonNull(root);
        Objects.requireNonNull(child);
        Objects.requireNonNull(rootX);
        Objects.requireNonNull(rootY);
        Objects.requireNonNull(winX);
        Objects.requireNonNull(winY);
        Objects.requireNonNull(mask);

        this.sameScreen = sameScreen;
        this.root = root;
        this.child = child;
        this.rootX = rootX;
        this.rootY = rootY;
        this.winX = winX;
        this.winY = winY;
        this.mask = mask;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
            "sameScreen", sameScreen,
            "root", root,
            "child", child,
            "rootX", rootX,
            "rootY", rootY,
            "winX", winX,
            "winY", winY,
            "mask", mask);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        stream.writeBOOL(sameScreen);
        
        writeSequenceNumber(stream);
        writeReplyLength(stream, 0);
        
        stream.writeWINDOW(root);
        stream.writeWINDOW(child);
        
        stream.writeINT16(rootX);
        stream.writeINT16(rootY);
        stream.writeINT16(winX);
        stream.writeINT16(winY);
        
        stream.writeSETofKEYBUTMASK(mask);
        
        stream.pad(6);
    }
}
