package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class CreateCursor extends XRequest {

    private final CURSOR cid;
    private final PIXMAP source;
    private final PIXMAP mask;
    
    private final CARD16 foreRed;
    private final CARD16 foreGreen;
    private final CARD16 foreBlue;
    
    private final CARD16 backRed;
    private final CARD16 backGreen;
    private final CARD16 backBlue;
    
    private final CARD16 x;
    private final CARD16 y;
    
    public static CreateCursor decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new CreateCursor(
                stream.readCURSOR(),
                stream.readPIXMAP(),
                stream.readPIXMAP(),
                
                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD16(),

                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD16(),

                stream.readCARD16(),
                stream.readCARD16()
        );
    }
    
    public CreateCursor(CURSOR cid, PIXMAP source, PIXMAP mask, CARD16 foreRed, CARD16 foreGreen, CARD16 foreBlue,
            CARD16 backRed, CARD16 backGreen, CARD16 backBlue, CARD16 x, CARD16 y) {

        Objects.requireNonNull(cid);
        Objects.requireNonNull(source);
        Objects.requireNonNull(mask);
        
        Objects.requireNonNull(foreRed);
        Objects.requireNonNull(foreGreen);
        Objects.requireNonNull(foreBlue);

        Objects.requireNonNull(backRed);
        Objects.requireNonNull(backGreen);
        Objects.requireNonNull(backBlue);

        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        
        this.cid = cid;
        this.source = source;
        this.mask = mask;
        this.foreRed = foreRed;
        this.foreGreen = foreGreen;
        this.foreBlue = foreBlue;
        this.backRed = backRed;
        this.backGreen = backGreen;
        this.backBlue = backBlue;
        this.x = x;
        this.y = y;
    }

    public CURSOR getCID() {
        return cid;
    }

    public PIXMAP getSource() {
        return source;
    }

    public PIXMAP getMask() {
        return mask;
    }

    public CARD16 getForeRed() {
        return foreRed;
    }

    public CARD16 getForeGreen() {
        return foreGreen;
    }

    public CARD16 getForeBlue() {
        return foreBlue;
    }

    public CARD16 getBackRed() {
        return backRed;
    }

    public CARD16 getBackGreen() {
        return backGreen;
    }

    public CARD16 getBackBlue() {
        return backBlue;
    }

    public CARD16 getX() {
        return x;
    }

    public CARD16 getY() {
        return y;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "cid", cid,
                "source", source,
                "mask", mask,
                "foreRed", foreRed,
                "foreGreen", foreGreen,
                "foreBlue", foreBlue,
                "backRed", backRed,
                "backGreen", backGreen,
                "backBlue", backBlue,
                "x", x,
                "y", y
        );
    }


    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 8);
        
        stream.writeCURSOR(cid);
        stream.writePIXMAP(source);
        stream.writePIXMAP(mask);
    
        stream.writeCARD16(foreRed);
        stream.writeCARD16(foreGreen);
        stream.writeCARD16(foreBlue);

        stream.writeCARD16(backRed);
        stream.writeCARD16(backGreen);
        stream.writeCARD16(backBlue);

        stream.writeCARD16(x);
        stream.writeCARD16(y);
    }
    
    @Override
    public int getOpCode() {
        return OpCodes.CREATE_CURSOR;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
