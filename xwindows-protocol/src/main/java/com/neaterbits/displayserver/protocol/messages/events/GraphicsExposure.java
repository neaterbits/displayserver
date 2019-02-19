package com.neaterbits.displayserver.protocol.messages.events;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;

public final class GraphicsExposure extends XEvent {

	private final DRAWABLE drawable;
	private final CARD16 x;
	private final CARD16 y;
	private final CARD16 width;
	private final CARD16 height;
	
	private final CARD16 count;

	private final CARD8 majorOpcode;
	private final CARD16 minorOpcode;

	public GraphicsExposure(
	        CARD16 sequenceNumber,
			DRAWABLE drawable,
			CARD16 x, CARD16 y,
			CARD16 width, CARD16 height,
			CARD16 count,
			CARD8 majorOpcode, CARD16 minorOpcode) {

	    super(sequenceNumber);
	    
		this.drawable = drawable;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.count = count;
		this.majorOpcode = majorOpcode;
		this.minorOpcode = minorOpcode;
	}

    public DRAWABLE getDrawable() {
        return drawable;
    }

    public CARD16 getX() {
        return x;
    }

    public CARD16 getY() {
        return y;
    }

    public CARD16 getWidth() {
        return width;
    }

    public CARD16 getHeight() {
        return height;
    }

    public CARD16 getCount() {
        return count;
    }

    public CARD8 getMajorOpcode() {
        return majorOpcode;
    }

    public CARD16 getMinorOpcode() {
        return minorOpcode;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "drawable", drawable,
                "x", x,
                "y", y,
                "width", width,
                "height", height,
                "count", count,
                "majorOpcode", majorOpcode,
                "minorOpcode", minorOpcode
        );
    }



    @Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {
		
	    writeEventCode(stream);
	    writeUnusedByte(stream);
	    writeSequenceNumber(stream);
	    
		stream.writeDRAWABLE(drawable);
		stream.writeCARD16(x);
		stream.writeCARD16(y);
		stream.writeCARD16(width);
		stream.writeCARD16(height);
		stream.writeCARD16(minorOpcode);
        stream.writeCARD16(count);
        stream.writeCARD8(majorOpcode);
        
        stream.pad(11);
	}

    @Override
    public int getEventCode() {
        return Events.GRAPHICS_EXPOSURE;
    }
}
