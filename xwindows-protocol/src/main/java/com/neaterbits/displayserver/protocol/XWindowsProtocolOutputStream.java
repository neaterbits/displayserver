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
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;

public interface XWindowsProtocolOutputStream {

    default void writeOpCode(int opCode) throws IOException {
        writeBYTE(new BYTE((byte)opCode));
    }
    
	void writeINT8(INT8 value) throws IOException;
	void writeINT16(INT16 value) throws IOException;
	void writeINT32(INT32 value) throws IOException;
	
	void writeCARD8(CARD8 value) throws IOException;
	void writeCARD16(CARD16 value) throws IOException;
	void writeCARD32(CARD32 value) throws IOException;

	void writeBYTE(BYTE value) throws IOException;
	
	void writeBOOL(BOOL value) throws IOException;
	
	
	void writeSTRING8(String value) throws IOException;

	void writeTIMESTAMP(TIMESTAMP value) throws IOException;
	
	void writeKEYCODE(KEYCODE value) throws IOException;

	void writeKEYSYM(KEYSYM value) throws IOException;
	
	void writeBUTTON(BUTTON value) throws IOException;
	
	void writeWINDOW(WINDOW value) throws IOException;
	void writePIXMAP(PIXMAP value) throws IOException;
	void writeDRAWABLE(DRAWABLE value) throws IOException;

	void writeGCONTEXT(GCONTEXT value) throws IOException;

	void writeCOLORMAP(COLORMAP value) throws IOException;
	
	void writeCURSOR(CURSOR value) throws IOException;
	
    void writeFONT(FONT value) throws IOException;
    void writeFONTABLE(FONTABLE value) throws IOException;
	
    void writeATOM(ATOM value) throws IOException;
    
	void writeVISUALID(VISUALID value) throws IOException;

	void writeBITGRAVITY(BITGRAVITY value) throws IOException;
	void writeWINGRAVITY(WINGRAVITY value) throws IOException;
	
	void writeBITMASK(BITMASK value) throws IOException;
    void writeBITMASK16(BITMASK value) throws IOException;
	void writeSET32(SET32 value) throws IOException;
	void writeSETofEVENT(SETofEVENT value) throws IOException;
	void writeSETofDEVICEEVENT(SETofDEVICEEVENT value) throws IOException;
    void writeSETofPOINTEREVENT(SETofPOINTEREVENT value) throws IOException;
	void writeSETofKEYBUTMASK(SETofKEYBUTMASK value) throws IOException;

	void writeSETofKEYMASK(SETofKEYMASK value) throws IOException;

	void writeData(byte [] data) throws IOException;

    void writeData(byte [] data, int offset, int length) throws IOException;

    void pad(int padLength) throws IOException;
}

