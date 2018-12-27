package com.neaterbits.displayserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindows;

public class XClientWindows extends XWindows<XClientWindow> {

    private final Map<XWindow, XClient> creatingClientByWindow;

    public XClientWindows() {
        
        this.creatingClientByWindow = new HashMap<>();
    }

    
    void addClientWindow(XWindow xWindow, XClient creatingClient) {

        Objects.requireNonNull(creatingClient);

        super.addClientWindow(xWindow);
        
        creatingClientByWindow.put(xWindow, creatingClient);
    }
    
    @Override
    protected boolean removeClientWindow(XWindow xWindow) {

        final boolean removed = super.removeClientWindow(xWindow);
        
        if (removed) {
            if (creatingClientByWindow.remove(xWindow) == null) {
                throw new IllegalStateException();
            }
        }
        else {
            if (creatingClientByWindow.remove(xWindow) != null) {
                throw new IllegalStateException();
            }
        }

        return removed;
    }
}
