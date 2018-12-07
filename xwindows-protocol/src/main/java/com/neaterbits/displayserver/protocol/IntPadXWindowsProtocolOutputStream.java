package com.neaterbits.displayserver.protocol;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.INT32;
import com.neaterbits.displayserver.protocol.types.INT8;
import com.neaterbits.displayserver.protocol.types.KEYCODE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SET32;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;

public class IntPadXWindowsProtocolOutputStream implements XWindowsProtocolOutputStream {

    private final XWindowsProtocolOutputStream delegate;

    public IntPadXWindowsProtocolOutputStream(XWindowsProtocolOutputStream delegate) {
        
        Objects.requireNonNull(delegate);
        
        this.delegate = delegate;
    }

    @Override
    public void pad(int num) throws IOException {
        delegate.pad(num);
    }
    
    private <T> void writeWithPadding(T value, int size, FieldWriter<T> writer) throws IOException {
        
        if (size > 4) {
            throw new IllegalArgumentException();
        }
        
        pad(4 - size);
        
        writer.write(value);
    }
    
    @Override
    public void writeINT8(INT8 value) throws IOException {
        writeWithPadding(value, 1, delegate::writeINT8);
    }

    @Override
    public void writeINT16(INT16 value) throws IOException {
        writeWithPadding(value, 2, delegate::writeINT16);
    }

    @Override
    public void writeINT32(INT32 value) throws IOException {
        delegate.writeINT32(value);
    }

    @Override
    public void writeCARD8(CARD8 value) throws IOException {
        writeWithPadding(value, 1, delegate::writeCARD8);
    }

    @Override
    public void writeCARD16(CARD16 value) throws IOException {
        writeWithPadding(value, 2, delegate::writeCARD16);
    }

    public void writeCARD32(CARD32 value) throws IOException {
        delegate.writeCARD32(value);
    }

    @Override
    public void writeBYTE(BYTE value) throws IOException {
        writeWithPadding(value, 1, delegate::writeBYTE);
    }

    @Override
    public void writeBOOL(BOOL value) throws IOException {
        writeWithPadding(value, 1, delegate::writeBOOL);
    }

    @Override
    public void writeSTRING8(String value) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writeTIMESTAMP(TIMESTAMP value) throws IOException {
        delegate.writeTIMESTAMP(value);
    }

    @Override
    public void writeKEYCODE(KEYCODE value) throws IOException {
        writeWithPadding(value, 1, delegate::writeKEYCODE);
    }

    @Override
    public void writeWINDOW(WINDOW value) throws IOException {
        delegate.writeWINDOW(value);
    }

    @Override
    public void writePIXMAP(PIXMAP value) throws IOException {
        delegate.writePIXMAP(value);
    }

    @Override
    public void writeDRAWABLE(DRAWABLE value) throws IOException {
        delegate.writeDRAWABLE(value);
    }

    @Override
    public void writeGCONTEXT(GCONTEXT value) throws IOException {
        delegate.writeGCONTEXT(value);
    }

    @Override
    public void writeCOLORMAP(COLORMAP value) throws IOException {
        delegate.writeCOLORMAP(value);
    }

    @Override
    public void writeCURSOR(CURSOR value) throws IOException {
        delegate.writeCURSOR(value);
    }

    @Override
    public void writeFONT(FONT value) throws IOException {
        delegate.writeFONT(value);
    }

    @Override
    public void writeATOM(ATOM value) throws IOException {
        delegate.writeATOM(value);
    }

    @Override
    public void writeVISUALID(VISUALID value) throws IOException {
        delegate.writeVISUALID(value);
    }
    
    @Override
    public void writeBITGRAVITY(BITGRAVITY value) throws IOException {
        delegate.writeBITGRAVITY(value);
    }

    @Override
    public void writeWINGRAVITY(WINGRAVITY value) throws IOException {
        delegate.writeWINGRAVITY(value);
    }

    @Override
    public void writeBITMASK(BITMASK value) throws IOException {
        delegate.writeBITMASK(value);
    }

    @Override
    public void writeSET32(SET32 value) throws IOException {
        delegate.writeSET32(value);
    }

    @Override
    public void writeSETofEVENT(SETofEVENT value) throws IOException {
        delegate.writeSETofEVENT(value);
    }

    @Override
    public void writeSETofDEVICEEVENT(SETofDEVICEEVENT value) throws IOException {
        delegate.writeSETofDEVICEEVENT(value);
    }

    @Override
    public void writeData(byte[] data) throws IOException {
        delegate.writeData(data);
    }

    @Override
    public void writeData(byte[] data, int offset, int length) throws IOException {
        delegate.writeData(data, offset, length);
    }
}
