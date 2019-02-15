package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;

public final class CARD8 extends XEncodeable {

	private final short value;

	public CARD8(short value) {
		
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		
		this.value = value;
	}

	public short getValue() {
		return value;
	}

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeCARD8(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
