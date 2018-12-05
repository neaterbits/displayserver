package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.OpCodes;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class MapWindow extends Request {

	private final WINDOW window;
	
	public MapWindow(WINDOW window) {
		this.window = window;
	}

	public static MapWindow decode(XWindowsProtocolInputStream stream) throws IOException {
		return new MapWindow(stream.readWINDOW());
	}

	@Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {
	    stream.writeOpCode(OpCodes.MAP_WINDOW);
	    stream.writeBYTE(new BYTE((byte)0));
	    stream.writeCARD16(new CARD16(8));
		stream.writeWINDOW(window);
	}
}
