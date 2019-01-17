package com.neaterbits.displayserver.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindows;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public class XClientWindows extends XWindows<XClientWindow> {

    private final Map<XWindow, XClientOps> creatingClientByWindow;

    public XClientWindows() {
        
        this.creatingClientByWindow = new HashMap<>();
    }

    
    public void addClientWindow(XWindow xWindow, XClientOps creatingClient) {

        Objects.requireNonNull(creatingClient);

        super.addClientWindow(xWindow);
        
        creatingClientByWindow.put(xWindow, creatingClient);
    }
    
    @Override
    public boolean removeClientWindow(XWindow xWindow) {

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
