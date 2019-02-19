package com.neaterbits.displayserver.events.common;

public final class KeyPressEvent extends KeyEvent {

	public KeyPressEvent(int keyCode, int modifier, int modifiersState) {
		super(keyCode, modifier, modifiersState);
	}
}
