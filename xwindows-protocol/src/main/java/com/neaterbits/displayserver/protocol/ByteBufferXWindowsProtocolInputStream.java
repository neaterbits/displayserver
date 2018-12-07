package com.neaterbits.displayserver.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
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
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;

public class ByteBufferXWindowsProtocolInputStream implements XWindowsProtocolInputStream {

	private final ByteBuffer buffer;
	
	public ByteBufferXWindowsProtocolInputStream(ByteBuffer buffer) {

		Objects.requireNonNull(buffer);
		
		this.buffer = buffer;
	}
	
	@Override
	public INT8 readINT8() {
		return new INT8(buffer.get());
	}

	@Override
	public INT16 readINT16() {
		return new INT16(buffer.getShort());
	}

	@Override
	public INT32 readINT32() {
		return new INT32(buffer.getInt());
	}

	@Override
	public CARD8 readCARD8() {
		final byte value = buffer.get();
		
		return new CARD8((short)Unsigned.byteToUnsigned(value));
	}

	@Override
	public CARD16 readCARD16() {
		final short value = buffer.getShort();

		return new CARD16(Unsigned.shortToUnsigned(value));
	}

	@Override
	public CARD32 readCARD32() {
		final int value = buffer.getInt();
		
		return new CARD32(Unsigned.intToUnsigned(value));
	}

	@Override
	public BYTE readBYTE() {
		return new BYTE(buffer.get());
	}
	
	@Override
    public BOOL readBOOL() throws IOException {
        return new BOOL(buffer.get());
    }

    @Override
	public String readSTRING8(int length) {

		final StringBuilder sb = new StringBuilder(length);
		
		for (int i = 0; i < length; ++ i) {
		    final byte value = buffer.get();

			final int unsigned = Unsigned.byteToUnsigned(value);
		
			sb.append((char)unsigned);
		}
		
		
		/*
		final int pad = XWindowsProtocolUtil.getPadding(length);

		for (int i = 0; i < pad; ++ i) {
			buffer.get();
		}
		*/
		
		return sb.toString();
	}

    @Override
    public TIMESTAMP readTIMESTAMP() throws IOException {
        return new TIMESTAMP(buffer.getInt());
    }

    @Override
	public KEYCODE readKEYCODE() throws IOException {
		return new KEYCODE(readCARD8());
	}

	private int readIdentifier() {
		return buffer.getInt();
	}
	
	@Override
	public WINDOW readWINDOW() throws IOException {
		return new WINDOW(readIdentifier());
	}

	@Override
    public PIXMAP readPIXMAP() throws IOException {
        return new PIXMAP(readIdentifier());
    }

    @Override
    public DRAWABLE readDRAWABLE() throws IOException {
        return new DRAWABLE(readIdentifier());
    }

    @Override
    public GCONTEXT readGCONTEXT() throws IOException {
        return new GCONTEXT(readIdentifier());
    }

    @Override
	public COLORMAP readCOLORMAP() throws IOException {
		return new COLORMAP(readIdentifier());
	}

	@Override
    public CURSOR readCURSOR() throws IOException {
        return new CURSOR(readIdentifier());
    }
	
    @Override
    public FONT readFONT() throws IOException {
        return new FONT(readIdentifier());
    }

    @Override
    public ATOM readATOM() throws IOException {
        return new ATOM(readIdentifier());
    }

    @Override
	public VISUALID readVISUALID() throws IOException {
		return new VISUALID(readIdentifier());
	}

	@Override
    public BITGRAVITY readBITGRAVITY() throws IOException {
        return new BITGRAVITY(buffer.get());
    }

    @Override
    public WINGRAVITY readWINGRAVITY() throws IOException {
        return new WINGRAVITY(buffer.get());
    }

    @Override
    public BITMASK readBITMASK() throws IOException {
        return new BITMASK(buffer.getInt());
    }

    @Override
	public SET32 readSET32() throws IOException {
		return new SET32(buffer.getInt());
	}

    @Override
    public SETofEVENT readSETofEVENT() throws IOException {
        return new SETofEVENT(buffer.getInt());
    }

    @Override
    public SETofDEVICEEVENT readSETofDEVICEEVENT() throws IOException {
        return new SETofDEVICEEVENT(buffer.getInt());
    }
    
    @Override
    public SETofKEYBUTMASK readSETofKEYBUTMASK() throws IOException {
        return new SETofKEYBUTMASK(buffer.getShort());
    }

    @Override
    public byte[] readData(int length) throws IOException {
        
        if (length > buffer.remaining()) {
            throw new IOException();
        }
        
        final byte [] data = new byte[length];
        
        buffer.get(data);
        
        return data;
    }

    @Override
    public void readPad(int length) throws IOException {
        
        for (int i = 0; i < length; ++ i) {
            buffer.get();
        }
    }
}
