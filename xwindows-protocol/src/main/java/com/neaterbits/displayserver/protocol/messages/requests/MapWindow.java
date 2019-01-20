package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class MapWindow extends XRequest {

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

	public WINDOW getWindow() {
        return window;
    }

    @Override
    public Object[] getDebugParams() {
	    return wrap("window", window);
    }

    @Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);

	    writeUnusedByte(stream);
	    
	    writeRequestLength(stream, 2);
	    
		stream.writeWINDOW(window);
	}

    @Override
    public int getOpCode() {
        return OpCodes.MAP_WINDOW;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
