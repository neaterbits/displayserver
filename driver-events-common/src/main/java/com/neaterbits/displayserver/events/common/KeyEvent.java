package com.neaterbits.displayserver.events.common;

public abstract class KeyEvent {

	private final int keyCode;

	public KeyEvent(int keyCode) {
		this.keyCode = keyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}
}
