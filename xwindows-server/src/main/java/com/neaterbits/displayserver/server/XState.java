package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.model.XDisplayState;
import com.neaterbits.displayserver.xwindows.model.XScreensAndVisuals;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

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
    
    XEventSubscriptions getEventSubscriptions() {
        return eventSubscriptions;
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
    public Iterable<XClientOps> getClientsInterestedInEvent(WINDOW window, int event) {
        return eventSubscriptions.getClientsInterestedInEvent(window, event);
    }

    @Override
    public XClientOps getSingleClientInterestedInEvent(WINDOW window, int event) {
        return eventSubscriptions.getSingleClientInterestedInEvent(window, event);
    }

    @Override
    public int getAllEventMasks(WINDOW window) {
        return eventSubscriptions.getAllEventMasks(window);
    }

    @Override
    public int getYourEventMask(WINDOW window, XClientOps client) {
        return eventSubscriptions.getYourEventMask(window, client);
    }
}
