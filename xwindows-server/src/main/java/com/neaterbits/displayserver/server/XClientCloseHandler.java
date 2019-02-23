package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.xwindows.model.XWindow;

public final class XClientCloseHandler {

    private final XFocusState xFocusState;
    private final XEventSubscriptions xEventSubscriptions;

    public XClientCloseHandler(XFocusState xFocusState, XEventSubscriptions xEventSubscriptions) {
        this.xFocusState = xFocusState;
        this.xEventSubscriptions = xEventSubscriptions;
    }

    public void onDestroyWindow(XWindow xWindow) {

        Objects.requireNonNull(xWindow);
    
        if (xFocusState.getInputFocus().equals(xWindow.getWINDOW())) {
            xFocusState.setInputFocus(null, null);
        }

        xEventSubscriptions.removeEventMappings(xWindow.getWINDOW());
    }
}
