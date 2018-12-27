package com.neaterbits.displayserver.xwindows.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.buffers.PixelFormat;

public final class XScreens implements XScreensConstAccess {

    private final List<XScreen> screens;

    public XScreens(List<XScreen> screens) {
        this.screens = new ArrayList<>(screens);
    }

    List<XScreen> getScreens() {
        return Collections.unmodifiableList(screens);
    }
    
    @Override
    public int getNumberOfScreens() {
        return screens.size();
    }

    @Override
    public XScreen getScreen(int screenNo) {
        return screens.get(screenNo);
    }

    @Override
    public Set<PixelFormat> getDistinctPixelFormats() {

        final Set<PixelFormat> distinctPixelFormats = this.screens.stream()
                .map(screen -> screen.getDisplayArea().getPixelFormat())
                .collect(Collectors.toSet());

        return distinctPixelFormats;
    }
}
