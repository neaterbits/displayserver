package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CURSOR;

public final class RecolorCursor extends Request {

    private final CURSOR cursor;
    
    private final CARD16 foreRed;
    private final CARD16 foreGreen;
    private final CARD16 foreBlue;
    
    private final CARD16 backRed;
    private final CARD16 backGreen;
    private final CARD16 backBlue;
    
    public static RecolorCursor decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        return new RecolorCursor(
                stream.readCURSOR(),
                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD16(),
                stream.readCARD16());
    }
    
    public RecolorCursor(
            CURSOR cursor, 
            CARD16 foreRed, CARD16 foreGreen, CARD16 foreBlue,
            CARD16 backRed, CARD16 backGreen, CARD16 backBlue) {

        Objects.requireNonNull(cursor);
        
        Objects.requireNonNull(foreRed);
        Objects.requireNonNull(foreGreen);
        Objects.requireNonNull(foreBlue);

        Objects.requireNonNull(backRed);
        Objects.requireNonNull(backGreen);
        Objects.requireNonNull(backBlue);

        this.cursor = cursor;
        this.foreRed = foreRed;
        this.foreGreen = foreGreen;
        this.foreBlue = foreBlue;
        this.backRed = backRed;
        this.backGreen = backGreen;
        this.backBlue = backBlue;
    }

    public CURSOR getCursor() {
        return cursor;
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

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "cursor", cursor,
                "foreRed", foreRed,
                "foreGreen", foreGreen,
                "foreBlue", foreBlue,
                "backRed", backRed,
                "backGreen", backGreen,
                "backBlue", backBlue
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        writeRequestLength(stream, 5);
        
        stream.writeCURSOR(cursor);
        
        stream.writeCARD16(foreRed);
        stream.writeCARD16(foreGreen);
        stream.writeCARD16(foreBlue);

        stream.writeCARD16(backRed);
        stream.writeCARD16(backGreen);
        stream.writeCARD16(backBlue);
    }

    @Override
    public int getOpCode() {
        return OpCodes.RECOLOR_CURSOR;
    }
}

