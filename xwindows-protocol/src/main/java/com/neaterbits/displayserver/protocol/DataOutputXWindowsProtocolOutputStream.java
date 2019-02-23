package com.neaterbits.displayserver.protocol;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BITMASK;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BUTTON;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.CHAR2B;
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
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.protocol.types.RESOURCE;
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

public final class DataOutputXWindowsProtocolOutputStream implements XWindowsProtocolOutputStream {

	private final DataOutput dataOutput;

	public DataOutputXWindowsProtocolOutputStream(DataOutput dataOutput) {

		Objects.requireNonNull(dataOutput);
		
		this.dataOutput = dataOutput;
	}

	@Override
	public void writeINT8(INT8 value) throws IOException {
		dataOutput.writeByte(value.getValue());
	}

	@Override
	public void writeINT16(INT16 value) throws IOException {
		dataOutput.writeShort(value.getValue());
	}

	@Override
	public void writeINT32(INT32 value) throws IOException {
		dataOutput.writeInt(value.getValue());
	}

	@Override
	public void writeCARD8(CARD8 value) throws IOException {
		dataOutput.writeByte((byte)value.getValue());
	}

	@Override
	public void writeCARD16(CARD16 value) throws IOException {
		dataOutput.writeShort((short)value.getValue());
	}

	@Override
	public void writeCARD32(CARD32 value) throws IOException {
		dataOutput.writeInt((int)value.getValue());
	}

	@Override
	public void writeBYTE(BYTE value) throws IOException {
		dataOutput.writeByte(value.getValue());
	}
	
	@Override
	public void writeBOOL(BOOL value) throws IOException {
		dataOutput.writeByte(value.getValue());
	}

	@Override
	public void writeSTRING8(String value) throws IOException {
		for (int i = 0; i < value.length(); ++ i) {
			dataOutput.writeByte((byte)(int)value.charAt(i));
		}

		/*
		final int pad = XWindowsProtocolUtil.getPadding(value.length());
		
		for (int i = 0; i < pad; ++ i) {
			dataOutput.writeByte(0);
		}
		*/
	}
	
	@Override
    public void writeSTRING16(STRING16 value) throws IOException {

	    for (int i = 0; i < value.length(); ++ i) {
	        
	        final CHAR2B character = value.getCharacter(i);
	        
	        dataOutput.writeByte(character.getByte1());
	        dataOutput.writeByte(character.getByte2());
	    }
	}

    @Override
    public void writeTIMESTAMP(TIMESTAMP value) throws IOException {
	    dataOutput.writeInt((int)value.getValue());
    }

    private void writeRESOURCE(RESOURCE resource) throws IOException {
		dataOutput.writeInt(resource.getValue());
	}
	
	@Override
	public void writeKEYCODE(KEYCODE value) throws IOException {
		dataOutput.writeByte(value.getValue());
	}
	
	@Override
    public void writeKEYSYM(KEYSYM value) throws IOException {
	    dataOutput.writeInt(value.getValue());
    }

    @Override
    public void writeBUTTON(BUTTON value) throws IOException {
	    dataOutput.writeByte(value.getValue());
    }

    @Override
	public void writeWINDOW(WINDOW value) throws IOException {
		writeRESOURCE(value);
	}
	
	@Override
	public void writePIXMAP(PIXMAP value) throws IOException {
		writeRESOURCE(value);
	}

	@Override
	public void writeDRAWABLE(DRAWABLE value) throws IOException {
		writeRESOURCE(value);
	}

	@Override
	public void writeGCONTEXT(GCONTEXT value) throws IOException {
		writeRESOURCE(value);
	}

	@Override
	public void writeCOLORMAP(COLORMAP value) throws IOException {
        writeRESOURCE(value);
	}
	
	@Override
    public void writeCURSOR(CURSOR value) throws IOException {
	    writeRESOURCE(value);
    }

    @Override
    public void writeFONT(FONT value) throws IOException {
        writeRESOURCE(value);
    }
    
    @Override
    public void writeFONTABLE(FONTABLE value) throws IOException {
        writeRESOURCE(value);
    }

    @Override
    public void writeATOM(ATOM value) throws IOException {
        dataOutput.writeInt(value.getValue());
    }

    @Override
	public void writeVISUALID(VISUALID value) throws IOException {
		dataOutput.writeInt(value.getValue());
	}

	@Override
    public void writeBITGRAVITY(BITGRAVITY value) throws IOException {
	    dataOutput.writeByte(value.getValue());
	}

    @Override
    public void writeWINGRAVITY(WINGRAVITY value) throws IOException {
        dataOutput.writeByte(value.getValue());
    }

    @Override
    public void writeBITMASK(BITMASK value) throws IOException {
        dataOutput.writeInt(value.getValue());
    }
    
    @Override
    public void writeBITMASK16(BITMASK value) throws IOException {
        
        final int intValue = value.getValue();
        
        if (intValue > Short.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        
        System.out.println("### write bitmask " + intValue);
        
        dataOutput.writeShort((short)intValue);
    }

    @Override
	public void writeSET32(SET32 value) throws IOException {
		dataOutput.writeInt(value.getValue());
	}

    @Override
    public void writeSETofEVENT(SETofEVENT value) throws IOException {
        dataOutput.writeInt(value.getValue());
    }

    @Override
    public void writeSETofDEVICEEVENT16(SETofDEVICEEVENT value) throws IOException {
        dataOutput.writeShort(value.getValue());
    }
    
    @Override
    public void writeSETofDEVICEEVENT32(SETofDEVICEEVENT value) throws IOException {
        dataOutput.writeInt(value.getValue());
    }

    @Override
    public void writeSETofPOINTEREVENT(SETofPOINTEREVENT value) throws IOException {
        dataOutput.writeShort((short)value.getValue());
    }

    @Override
    public void writeSETofKEYBUTMASK(SETofKEYBUTMASK value) throws IOException {
        dataOutput.writeShort(value.getValue());
    }

    @Override
    public void writeSETofKEYMASK(SETofKEYMASK value) throws IOException {
        dataOutput.writeShort(value.getValue());
    }

    @Override
    public void writePOINT(POINT point) throws IOException {
        dataOutput.writeShort(point.getX());
        dataOutput.writeShort(point.getY());
    }

    @Override
    public void writeData(byte[] data) throws IOException {
        dataOutput.write(data);
    }

    @Override
    public void writeData(byte[] data, int offset, int length) throws IOException {
        dataOutput.write(data, offset, length);
    }

    @Override
    public void pad(int padLength) throws IOException {
        for (int i = 0; i < padLength; ++ i) {
            writeBYTE(new BYTE((byte)0));
        }
    }
}
