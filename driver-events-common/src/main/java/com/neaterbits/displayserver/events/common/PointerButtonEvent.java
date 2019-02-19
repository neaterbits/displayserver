package com.neaterbits.displayserver.events.common;

public abstract class PointerButtonEvent extends PointerEvent {

    private final int button;

    public PointerButtonEvent(int button) {
        this.button = button;
    }

    public final int getButton() {
        return button;
    }
}
