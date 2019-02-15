package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.TranslateCoordinatesReply;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class TranslateCoordinates extends XRequest {

    private final WINDOW srcWindow;
    private final WINDOW dstWindow;
    
    private final INT16 srcX;
    private final INT16 srcY;

    public static TranslateCoordinates decode(XWindowsProtocolInputStream stream) throws IOException {

        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new TranslateCoordinates(
                stream.readWINDOW(),
                stream.readWINDOW(),
                stream.readINT16(),
                stream.readINT16());
        
    }
    
    public TranslateCoordinates(WINDOW srcWindow, WINDOW dstWindow, INT16 srcX, INT16 srcY) {
    
        Objects.requireNonNull(srcWindow);
        Objects.requireNonNull(dstWindow);
        Objects.requireNonNull(srcX);
        Objects.requireNonNull(srcY);
        
        this.srcWindow = srcWindow;
        this.dstWindow = dstWindow;
        this.srcX = srcX;
        this.srcY = srcY;
    }
    
    public WINDOW getSrcWindow() {
        return srcWindow;
    }

    public WINDOW getDstWindow() {
        return dstWindow;
    }

    public INT16 getSrcX() {
        return srcX;
    }

    public INT16 getSrcY() {
        return srcY;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "srcWindow", srcWindow,
                "dstWindow", dstWindow,
                "srcX", srcX,
                "srcY", srcY
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 4);
        
        stream.writeWINDOW(srcWindow);
        stream.writeWINDOW(dstWindow);
        
        stream.writeINT16(srcX);
        stream.writeINT16(srcY);
    }

    @Override
    public int getOpCode() {
        return OpCodes.TRANSLATE_COORDINATES;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return TranslateCoordinatesReply.class;
    }
}
