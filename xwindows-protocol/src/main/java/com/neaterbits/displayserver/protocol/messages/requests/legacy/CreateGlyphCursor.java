package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.FONT;

public final class CreateGlyphCursor extends Request {

    private final CURSOR cid;
    private final FONT sourceFont;
    private final FONT maskFont;
    
    private final CARD16 sourceChar;
    private final CARD16 maskChar;
    
    private final CARD16 foreRed;
    private final CARD16 foreGreen;
    private final CARD16 foreBlue;
    
    private final CARD16 backRed;
    private final CARD16 backGreen;
    private final CARD16 backBlue;
    
    public static CreateGlyphCursor decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
    
        return new CreateGlyphCursor(
                stream.readCURSOR(),
                stream.readFONT(), stream.readFONT(),
                stream.readCARD16(), stream.readCARD16(),
                stream.readCARD16(), stream.readCARD16(), stream.readCARD16(),
                stream.readCARD16(), stream.readCARD16(), stream.readCARD16());
    }
    
    public CreateGlyphCursor(
            CURSOR cid,
            FONT sourceFont, FONT maskFont,
            CARD16 sourceChar, CARD16 maskChar,
            CARD16 foreRed, CARD16 foreGreen, CARD16 foreBlue,
            CARD16 backRed, CARD16 backGreen, CARD16 backBlue) {

        Objects.requireNonNull(cid);
        
        Objects.requireNonNull(sourceFont);
        Objects.requireNonNull(maskFont);
        
        Objects.requireNonNull(sourceChar);
        Objects.requireNonNull(maskChar);
        
        Objects.requireNonNull(foreRed);
        Objects.requireNonNull(foreGreen);
        Objects.requireNonNull(foreBlue);

        Objects.requireNonNull(backRed);
        Objects.requireNonNull(backGreen);
        Objects.requireNonNull(backBlue);
        
        this.cid = cid;
        this.sourceFont = sourceFont;
        this.maskFont = maskFont;
        this.sourceChar = sourceChar;
        this.maskChar = maskChar;
        this.foreRed = foreRed;
        this.foreGreen = foreGreen;
        this.foreBlue = foreBlue;
        this.backRed = backRed;
        this.backGreen = backGreen;
        this.backBlue = backBlue;
    }

    public CURSOR getCid() {
        return cid;
    }

    public FONT getSourceFont() {
        return sourceFont;
    }

    public FONT getMaskFont() {
        return maskFont;
    }

    public CARD16 getSourceChar() {
        return sourceChar;
    }

    public CARD16 getMaskChar() {
        return maskChar;
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
                
                "cid", cid,
                "sourceFont", sourceFont,
                "maskFont", maskFont,
                "sourceChar", sourceChar,
                "maskChar", maskChar,
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
        
        writeRequestLength(stream, 8);
        
        
        stream.writeCURSOR(cid);
        
        stream.writeFONT(sourceFont);
        stream.writeFONT(maskFont);
        
        stream.writeCARD16(sourceChar);
        stream.writeCARD16(maskChar);

        stream.writeCARD16(foreRed);
        stream.writeCARD16(foreGreen);
        stream.writeCARD16(foreBlue);

        stream.writeCARD16(backRed);
        stream.writeCARD16(backGreen);
        stream.writeCARD16(backBlue);
    }

    @Override
    public int getOpCode() {
        return OpCodes.CREATE_GLYPH_CURSOR;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return null;
    }
}
