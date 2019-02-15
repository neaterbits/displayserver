package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class DestroyWindow extends XRequest {

	private final WINDOW window;

	public DestroyWindow(WINDOW window) {
		
		Objects.requireNonNull(window);
		
		this.window = window;
	}
	
	public WINDOW getWindow() {
		return window;
	}

	public static DestroyWindow decode(XWindowsProtocolInputStream stream) throws IOException {
		return new DestroyWindow(stream.readWINDOW());
	}

	@Override
    public Object[] getDebugParams() {
        return wrap("window", window);
    }

    @Override
	public void encode(XWindowsProtocolOutputStream stream) throws IOException {

	    writeOpCode(stream);
	    
	    stream.writeWINDOW(window);
	    
	    writeRequestLength(stream, 2);
	}

    @Override
    public int getOpCode() {
        return OpCodes.DESTROY_WINDOW;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
