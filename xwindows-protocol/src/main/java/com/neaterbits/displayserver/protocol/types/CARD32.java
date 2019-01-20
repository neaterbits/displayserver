package com.neaterbits.displayserver.protocol.types;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;

public final class CARD32 extends XEncodeable {

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (value ^ (value >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CARD32 other = (CARD32) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
