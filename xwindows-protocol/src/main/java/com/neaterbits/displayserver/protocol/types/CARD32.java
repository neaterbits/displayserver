package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;

public final class CARD32 extends Encodeable {

	private final long value;

	public CARD32(long value) {
		
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		
		this.value = value;
	}

	public long getValue() {
		return value;
	}

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeCARD32(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
