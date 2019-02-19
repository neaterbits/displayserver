package com.neaterbits.displayserver.events.common;

public abstract class KeyEvent extends InputEvent {

	private final int keyCode;

	// set if key is a modifier
	private final int modifier;

	// State of all modifiers
	private final int modifiersState;

	public KeyEvent(int keyCode, int modifier, int modifiersState) {
		this.keyCode = keyCode;
		this.modifier = modifier;
		this.modifiersState = modifiersState;
	}

	public final int getKeyCode() {
		return keyCode;
	}

    public final int getModifier() {
        return modifier;
    }

    public final int getModifiersState() {
        return modifiersState;
    }
}
