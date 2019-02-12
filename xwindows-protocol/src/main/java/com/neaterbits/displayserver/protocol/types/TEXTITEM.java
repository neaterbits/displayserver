package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;

public abstract class TEXTITEM<STRING, ITEM extends TEXTITEM<STRING, ITEM>> extends XEncodeable {

    private final BYTE length;
    private final INT8 delta;
    private final STRING string;
    
    private final BYTE fontShiftIndicator;
    private final BYTE fontByte3;
    private final BYTE fontByte2;
    private final BYTE fontByte1;
    private final BYTE fontByte0;

    abstract void encodeString(XWindowsProtocolOutputStream stream, STRING string) throws IOException;

    abstract int getNumberOfEncodedBytes(STRING string);

    @FunctionalInterface
    interface CreateString<STRING, ITEM> {
        ITEM create(BYTE length, INT8 delta, STRING string);
    }
    
    @FunctionalInterface
    interface DecodeString<STRING> {
        STRING decode(XWindowsProtocolInputStream stream, int length) throws IOException;
    }

    @FunctionalInterface
    interface CreateFontShift<ITEM> {
        ITEM create(BYTE fontShiftIndicator, BYTE fontByte3, BYTE fontByte2, BYTE fontByte1, BYTE fontByte0);
    }

    static <STRING, ITEM extends TEXTITEM<STRING, ITEM>> ITEM decode(
            XWindowsProtocolInputStream stream,
            CreateString<STRING, ITEM> createString,
            DecodeString<STRING> decodeString,
            CreateFontShift<ITEM> createFontShift
            ) throws IOException {
        
        final ITEM item;
        
        final BYTE b = stream.readBYTE();
        
        if (b.getValue() != 255) {

            final INT8 delta = stream.readINT8();
            
            final STRING string = decodeString.decode(stream, b.getValue());
            
            item = createString.create(b, delta, string);
        }
        else {
            item = createFontShift.create(
                    stream.readBYTE(),
                    stream.readBYTE(),
                    stream.readBYTE(),
                    stream.readBYTE(),
                    stream.readBYTE());
        }
        
        return item;
    }
    
    TEXTITEM(BYTE length, INT8 delta, STRING string) {

        Objects.requireNonNull(length);
        Objects.requireNonNull(delta);
        Objects.requireNonNull(string);
        
        this.length = length;
        this.delta = delta;
        this.string = string;

        this.fontShiftIndicator = null;
        this.fontByte3 = null;
        this.fontByte2 = null;
        this.fontByte1 = null;
        this.fontByte0 = null;
    }

    TEXTITEM(BYTE fontShiftIndicator, BYTE fontByte3, BYTE fontByte2, BYTE fontByte1, BYTE fontByte0) {

        Objects.requireNonNull(fontShiftIndicator);
        Objects.requireNonNull(fontByte3);
        Objects.requireNonNull(fontByte2);
        Objects.requireNonNull(fontByte1);
        Objects.requireNonNull(fontByte0);
        
        if (fontShiftIndicator.getValue() != 255) {
            throw new IllegalArgumentException();
        }
        
        this.length = null;
        
        this.delta = null;
        this.string = null;
        
        this.fontShiftIndicator = fontShiftIndicator;
        this.fontByte3 = fontByte3;
        this.fontByte2 = fontByte2;
        this.fontByte1 = fontByte1;
        this.fontByte0 = fontByte0;
    }

    public final BYTE getLength() {
        return length;
    }

    public final INT8 getDelta() {
        return delta;
    }

    public final STRING getString() {
        return string;
    }

    public final BYTE getFontShiftIndicator() {
        return fontShiftIndicator;
    }

    public final BYTE getFontByte3() {
        return fontByte3;
    }

    public final BYTE getFontByte2() {
        return fontByte2;
    }

    public final BYTE getFontByte1() {
        return fontByte1;
    }

    public final BYTE getFontByte0() {
        return fontByte0;
    }

    public final boolean isString() {
        return length != null;
    }
    
    public final int getNumberOfEncodedBytes() {
        return isString() ? 2 + getNumberOfEncodedBytes(string) : 6;
    }
    
    @Override
    public final Object[] getDebugParams() {

        return wrap(
                "length", length,
                "delta", delta,
                "string", string,
                "fontShiftIndicator", fontShiftIndicator,
                "fontByte3", fontByte3,
                "fontByte2", fontByte2,
                "fontByte1", fontByte1,
                "fontByte0", fontByte0
        );
    }

    @Override
    public final void encode(XWindowsProtocolOutputStream stream) throws IOException {

        stream.writeBYTE(length);
        
        if (isString()) {

            stream.writeINT8(delta);
            
            encodeString(stream, string);
        }
        else {
            stream.writeBYTE(fontShiftIndicator);

            stream.writeBYTE(fontByte3);
            stream.writeBYTE(fontByte2);
            stream.writeBYTE(fontByte1);
            stream.writeBYTE(fontByte0);
        }
    }
}
