package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.neaterbits.displayserver.buffers.PixelFormat;

final class XScreens implements XScreensConstAccess {

    private final List<XWindowsScreen> screens;

    XScreens(List<XWindowsScreen> screens) {
        this.screens = new ArrayList<>(screens);
    }

    List<XWindowsScreen> getScreens() {
        return Collections.unmodifiableList(screens);
    }
    
    @Override
    public int getNumberOfScreens() {
        return screens.size();
    }

    @Override
    public XWindowsScreen getScreen(int screenNo) {
        return screens.get(screenNo);
    }

    @Override
    public Set<PixelFormat> getDistinctPixelFormats() {

        final Set<PixelFormat> distinctPixelFormats = this.screens.stream()
                .map(screen -> screen.getScreen().getDriverScreen().getPixelFormat())
                .collect(Collectors.toSet());

        return distinctPixelFormats;
    }
}
