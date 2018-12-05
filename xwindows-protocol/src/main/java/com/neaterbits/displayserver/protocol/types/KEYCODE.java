package com.neaterbits.displayserver.protocol.types;

public final class KEYCODE {

	private final CARD8 value;

   public KEYCODE(short value) {
       this(new CARD8(value));
   }

	public KEYCODE(CARD8 value) {
		this.value = value;
	}
	
	public short getValue() {
		return value.getValue();
	}

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
