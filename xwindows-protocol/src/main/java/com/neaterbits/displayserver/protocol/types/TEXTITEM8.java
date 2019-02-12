package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;

public final class TEXTITEM8 extends TEXTITEM<String, TEXTITEM8> {

    public static TEXTITEM8 decode(XWindowsProtocolInputStream stream) throws IOException {
        return decode(
                stream,
                TEXTITEM8::new,
                XWindowsProtocolInputStream::readSTRING8,
                TEXTITEM8::new);
    }
    
    public TEXTITEM8(BYTE fontShiftIndicator, BYTE fontByte3, BYTE fontByte2, BYTE fontByte1, BYTE fontByte0) {
        super(fontShiftIndicator, fontByte3, fontByte2, fontByte1, fontByte0);
    }

    public TEXTITEM8(BYTE length, INT8 delta, String string) {
        super(length, delta, string);
    }

    @Override
    void encodeString(XWindowsProtocolOutputStream stream, String string) throws IOException {
        stream.writeSTRING8(string);
    }

    @Override
    int getNumberOfEncodedBytes(String string) {
        return string.length();
    }
}
