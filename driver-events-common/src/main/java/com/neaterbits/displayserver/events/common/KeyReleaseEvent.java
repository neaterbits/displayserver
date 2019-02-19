package com.neaterbits.displayserver.events.common;

public final class KeyReleaseEvent extends KeyEvent {

	public KeyReleaseEvent(int keyCode, int modifier, int modifiersState) {
		super(keyCode, modifier, modifiersState);
	}
}
