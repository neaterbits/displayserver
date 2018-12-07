package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class MapWindow extends Request {

	private final WINDOW window;
	
	public static MapWindow decode(XWindowsProtocolInputStream stream) throws IOException {
	    
	    readUnusedByte(stream);
	    
	    readRequestLength(stream);
	    
		return new MapWindow(stream.readWINDOW());
	}

    public MapWindow(WINDOW window) {
        
        Objects.requireNonNull(window);
        
        this.window = window;
    }

	@Override
    public Object[] getDebugParams() {
	    return wrap("window", window);
    }

    @Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {
	    
	    stream.writeOpCode(OpCodes.MAP_WINDOW);

	    writeUnusedByte(stream);
	    
	    writeRequestLength(stream, 8);
	    
		stream.writeWINDOW(window);
	}
}
