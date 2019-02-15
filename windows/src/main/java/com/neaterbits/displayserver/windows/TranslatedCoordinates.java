package com.neaterbits.displayserver.windows;

public final class TranslatedCoordinates {

    private final int x;
    private final int y;
    private final Window window;
    
    public TranslatedCoordinates(int x, int y, Window window) {
        this.x = x;
        this.y = y;
        this.window = window;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Window getWindow() {
        return window;
    }
}
