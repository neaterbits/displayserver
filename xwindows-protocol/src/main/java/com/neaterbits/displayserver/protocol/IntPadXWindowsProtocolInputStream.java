package com.neaterbits.displayserver.protocol;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BUTTON;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.FONTABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.INT32;
import com.neaterbits.displayserver.protocol.types.INT8;
import com.neaterbits.displayserver.protocol.types.KEYCODE;
import com.neaterbits.displayserver.protocol.types.KEYSYM;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SET32;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.SETofKEYMASK;
import com.neaterbits.displayserver.protocol.types.SETofPOINTEREVENT;
import com.neaterbits.displayserver.protocol.types.STRING16;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;

public class IntPadXWindowsProtocolInputStream implements XWindowsProtocolInputStream {

    private final XWindowsProtocolInputStream delegate;

    public IntPadXWindowsProtocolInputStream(XWindowsProtocolInputStream delegate) {
        this.delegate = delegate;
    }

    private <T> int readWithPadding() throws IOException {
        
        final CARD32 integer = delegate.readCARD32();
        
        return (int)integer.getValue();
    }

    @Override
    public INT8 readINT8() throws IOException {
        return new INT8((byte)readWithPadding());
    }

    @Override
    public INT16 readINT16() throws IOException {
        return new INT16((short)readWithPadding());
    }

    @Override
    public INT32 readINT32() throws IOException {
        return delegate.readINT32();
    }

    @Override
    public CARD8 readCARD8() throws IOException {
        return new CARD8((short)readWithPadding());
    }

    @Override
    public CARD16 readCARD16() throws IOException {
        return new CARD16(readWithPadding());
    }

    @Override
    public CARD32 readCARD32() throws IOException {
        return delegate.readCARD32();
    }

    @Override
    public BYTE readBYTE() throws IOException {
        return new BYTE((byte)readWithPadding());
    }

    @Override
    public BOOL readBOOL() throws IOException {
        return new BOOL((byte)readWithPadding());
    }

    @Override
    public String readSTRING8(int length) throws IOException {
        return delegate.readSTRING8(length);
    }
    
    @Override
    public STRING16 readSTRING16(int length) throws IOException {
        return delegate.readSTRING16(length);
    }

    @Override
    public TIMESTAMP readTIMESTAMP() throws IOException {
        return delegate.readTIMESTAMP();
    }

    @Override
    public KEYCODE readKEYCODE() throws IOException {
        return new KEYCODE((short)readWithPadding());
    }

    @Override
    public KEYSYM readKEYSYM() throws IOException {
        return delegate.readKEYSYM();
    }

    @Override
    public BUTTON readBUTTON() throws IOException {
        return new BUTTON((short)readWithPadding());
    }

    @Override
    public WINDOW readWINDOW() throws IOException {
        return delegate.readWINDOW();
    }

    @Override
    public PIXMAP readPIXMAP() throws IOException {
        return delegate.readPIXMAP();
    }
    
    @Override
    public DRAWABLE readDRAWABLE() throws IOException {
        return delegate.readDRAWABLE();
    }

    @Override
    public GCONTEXT readGCONTEXT() throws IOException {
        return delegate.readGCONTEXT();
    }

    @Override
    public COLORMAP readCOLORMAP() throws IOException {
        return delegate.readCOLORMAP();
    }

    @Override
    public CURSOR readCURSOR() throws IOException {
        return delegate.readCURSOR();
    }

    @Override
    public FONT readFONT() throws IOException {
        return delegate.readFONT();
    }
    
    @Override
    public FONTABLE readFONTABLE() throws IOException {
        return delegate.readFONTABLE();
    }

    @Override
    public ATOM readATOM() throws IOException {
        return delegate.readATOM();
    }

    @Override
    public VISUALID readVISUALID() throws IOException {
        return delegate.readVISUALID();
    }

    @Override
    public BITGRAVITY readBITGRAVITY() throws IOException {
        return new BITGRAVITY((byte)readWithPadding());
    }

    @Override
    public WINGRAVITY readWINGRAVITY() throws IOException {
        return new WINGRAVITY((byte)readWithPadding());
    }

    @Override
    public BITMASK readBITMASK() throws IOException {
        return delegate.readBITMASK();
    }

    @Override
    public BITMASK readBITMASK16() throws IOException {
        return delegate.readBITMASK16();
    }

    @Override
    public SET32 readSET32() throws IOException {
        return delegate.readSET32();
    }

    @Override
    public SETofEVENT readSETofEVENT() throws IOException {
        return delegate.readSETofEVENT();
    }

    @Override
    public SETofDEVICEEVENT readSETofDEVICEEVENT16() throws IOException {
        return new SETofDEVICEEVENT((short)readWithPadding());
    }

    @Override
    public SETofDEVICEEVENT readSETofDEVICEEVENT32() throws IOException {
        return delegate.readSETofDEVICEEVENT32();
    }
    
    @Override
    public SETofPOINTEREVENT readSETofPOINTEREVENT() throws IOException {
        return new SETofPOINTEREVENT(readWithPadding());
    }

    @Override
    public SETofKEYBUTMASK readSETofKEYBUTMASK() throws IOException {
        return new SETofKEYBUTMASK((short)readWithPadding());
    }
    
    @Override
    public SETofKEYMASK readSETofKEYMASK() throws IOException {
        return new SETofKEYMASK((short)readWithPadding());
    }

    @Override
    public byte[] readData(int length) throws IOException {
        return delegate.readData(length);
    }

    @Override
    public void readPad(int length) throws IOException {
        delegate.readPad(length);
    }
}
