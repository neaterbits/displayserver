package com.neaterbits.displayserver.server;

import java.util.List;
import java.util.Set;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

final class XState implements
    XScreensConstAccess,
    XClientsConstAccess,
    XWindowsConstAccess,
    XEventSubscriptionsConstAccess {

    private final XScreens screens;
    private final XClients clients;
    private final XWindows windows;
    private final XEventSubscriptions eventSubscriptions;
    
    XState(List<XWindowsScreen> screens) {

        this.screens = new XScreens(screens);
        
        this.clients = new XClients();
        
        this.windows = new XWindows();
        
        this.eventSubscriptions = new XEventSubscriptions();
    }

    @Override
    public int getNumberOfScreens() {
        return screens.getNumberOfScreens();
    }

    @Override
    public XWindowsScreen getScreen(int screenNo) {
        return screens.getScreen(screenNo);
    }

    @Override
    public Set<PixelFormat> getDistinctPixelFormats() {
        return screens.getDistinctPixelFormats();
    }

    @Override
    public Iterable<XClient> getClients() {
        return clients.getClients();
    }
    
    void addWindow(XWindow window, XClient creatingClient) {
        windows.addWindow(window, creatingClient);
    }

    void removeClientWindow(XWindow window) {
        windows.removeClientWindow(window);
    }
    
    @Override
    public XWindow getClientWindow(DRAWABLE windowResource) {
        return windows.getClientWindow(windowResource);
    }

    @Override
    public XWindow getClientWindow(WINDOW windowResource) {
        return windows.getClientWindow(windowResource);
    }

    @Override
    public XWindow findRootWindowOf(WINDOW windowResource) {
        return windows.findRootWindowOf(windowResource);
    }

    @Override
    public XWindow getClientWindow(Window window) {
        return windows.getClientWindow(window);
    }

    @Override
    public Iterable<XClient> getClientsInterestedInEvent(XWindow window, int event) {
        return eventSubscriptions.getClientsInterestedInEvent(window, event);
    }
}
