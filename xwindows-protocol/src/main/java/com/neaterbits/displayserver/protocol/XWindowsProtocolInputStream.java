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

public interface XWindowsProtocolInputStream {

	INT8  readINT8() throws IOException;
	INT16 readINT16() throws IOException;
	INT32 readINT32() throws IOException;
	
	CARD8  readCARD8() throws IOException;
	CARD16 readCARD16() throws IOException;
	CARD32 readCARD32() throws IOException;

	BYTE readBYTE() throws IOException;
	BOOL readBOOL() throws IOException;
	
	String readSTRING8(int length) throws IOException;

	TIMESTAMP readTIMESTAMP() throws IOException;
	
	KEYCODE readKEYCODE() throws IOException;
	
	KEYSYM readKEYSYM() throws IOException;
	
	BUTTON readBUTTON() throws IOException;
	
	WINDOW readWINDOW() throws IOException;
	PIXMAP readPIXMAP() throws IOException;
	DRAWABLE readDRAWABLE() throws IOException;
	
	GCONTEXT readGCONTEXT() throws IOException;
	
	COLORMAP readCOLORMAP() throws IOException;
	CURSOR readCURSOR() throws IOException;
	FONT readFONT() throws IOException;
    FONTABLE readFONTABLE() throws IOException;

	ATOM readATOM() throws IOException;
	
	VISUALID readVISUALID() throws IOException;
	
	BITGRAVITY readBITGRAVITY() throws IOException;
	WINGRAVITY readWINGRAVITY() throws IOException;
	
	BITMASK readBITMASK() throws IOException;
    BITMASK readBITMASK16() throws IOException;
	
	SET32 readSET32() throws IOException;

	SETofEVENT readSETofEVENT() throws IOException;
	SETofDEVICEEVENT readSETofDEVICEEVENT() throws IOException;
	SETofPOINTEREVENT readSETofPOINTEREVENT() throws IOException;
	SETofKEYBUTMASK readSETofKEYBUTMASK() throws IOException;
	SETofKEYMASK readSETofKEYMASK() throws IOException;
	
	byte [] readData(int length) throws IOException;
	
	void readPad(int length) throws IOException;
}
