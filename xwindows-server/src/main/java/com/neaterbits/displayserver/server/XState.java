package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.model.XDisplayState;
import com.neaterbits.displayserver.xwindows.model.XScreensAndVisuals;
import com.neaterbits.displayserver.xwindows.model.XWindow;

final class XState extends XDisplayState<XClientWindow, XClientWindows> implements
    XClientsConstAccess,
    XClientWindowsConstAccess,
    XEventSubscriptionsConstAccess {

    private final XClients clients;
    private final XEventSubscriptions eventSubscriptions;
    
    XState(XScreensAndVisuals screensAndVisuals) {
        super(screensAndVisuals, XClientWindows::new);

        this.clients = new XClients();
        
        this.eventSubscriptions = new XEventSubscriptions();
    }

    @Override
    public Iterable<XClient> getClients() {
        return clients.getClients();
    }
    
    void addRootWindow(int screen, XWindow window) {
        getWindows().addRootWindow(screen, window);
    }
    
    @Override
    public Integer getScreenForWindow(WINDOW window) {
        return getWindows().getScreenForWindow(window);
    }

    void addClientWindow(XWindow window, XClient creatingClient) {
        getWindows().addClientWindow(window, creatingClient);
    }

    void removeClientWindow(XWindow window) {
        getWindows().removeClientWindow(window);
    }
    
    @Override
    public Iterable<XClient> getClientsInterestedInEvent(XWindow window, int event) {
        return eventSubscriptions.getClientsInterestedInEvent(window, event);
    }
}
