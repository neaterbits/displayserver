package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

public final class XColormap {

    private final XScreen screen;
    private final XVisual visual;

    public XColormap(XScreen screen, XVisual visual) {

        Objects.requireNonNull(screen);
        Objects.requireNonNull(visual);
        
        this.screen = screen;
        this.visual = visual;
    }

    public XScreen getScreen() {
        return screen;
    }

    public XVisual getVisual() {
        return visual;
    }

    public int getVisualClass() {
        return visual.getVisualClass();
    }
}
