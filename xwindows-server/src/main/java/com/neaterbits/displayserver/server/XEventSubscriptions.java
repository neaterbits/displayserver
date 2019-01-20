package com.neaterbits.displayserver.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.exception.AccessException;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public final class XEventSubscriptions implements XEventSubscriptionsConstAccess {

    private final Map<Key, List<XClientOps>> eventToClients;
    
    public XEventSubscriptions() {

        this.eventToClients = new HashMap<>();
    }

    private void clearEventsForWindowAndClient(WINDOW window, XClientOps client) {

        Objects.requireNonNull(window);
        Objects.requireNonNull(client);
        
        final List<Key> emptyEntries = new ArrayList<>(eventToClients.size());
        
        for (Map.Entry<Key, List<XClientOps>> entry : eventToClients.entrySet()) {
            
            final Key key = entry.getKey();
            
            if (key.window.equals(window)) {
                
                final List<XClientOps> clients = entry.getValue();
                
                clients.remove(client);
                
                if (clients.isEmpty()) {
                    emptyEntries.add(key);
                }
            }
        }
        
        emptyEntries.forEach(eventToClients::remove);
    }
    
    public void setEventMapping(XWindow xWindow, SETofEVENT events, XClientOps client) throws AccessException {
        
        clearEventsForWindowAndClient(xWindow.getWINDOW(), client);

        for (int i = 0; i < 32; ++ i) {
            
            int flag = 1 << i;
            
            if ((events.getValue() & flag) != 0) {
                addEventMapping(xWindow, flag, client);
            }
        }
    }
    
    public void addEventMapping(XWindow xWindow, int event, XClientOps client) throws AccessException {
        
        Objects.requireNonNull(xWindow);
        Objects.requireNonNull(client);
        
        final Key key = new Key(xWindow.getWINDOW(), event);
        
        List<XClientOps> clientOps = eventToClients.get(key);
        
        if (clientOps == null) {
            clientOps = new ArrayList<>();

            eventToClients.put(key, clientOps);
        }
        else {
            
            if (clientOps.isEmpty()) {
                throw new IllegalStateException();
            }
            
            if (event == SETofEVENT.SUBSTRUCTURE_REDIRECT || event == SETofEVENT.RESIZE_REDIRECT) {
                throw new AccessException("Already subscribed to by another client");
            }
        }

        clientOps.add(client);
    }

    
    public void removeEventMapping(XWindow xWindow, int event, XClientOps client) throws AccessException {

        Objects.requireNonNull(xWindow);
        Objects.requireNonNull(client);

        final Key key = new Key(xWindow.getWINDOW(), event);
        
        List<XClientOps> clientOps = eventToClients.get(key);

        if (clientOps != null) {
            clientOps.remove(client);
            
            if (clientOps.isEmpty()) {
                eventToClients.remove(key);
            }
        }
    }

    @Override
    public Iterable<XClientOps> getClientsInterestedInEvent(WINDOW window, int event) {
        
        return getClientListInterestedInEvent(window, event);
    }

    private List<XClientOps> getClientListInterestedInEvent(WINDOW window, int event) {

        Objects.requireNonNull(window);
        
        final Key key = new Key(window, event);
        
        final List<XClientOps> clients = eventToClients.get(key);
        
        return clients != null ? clients : Collections.emptyList();
    }
    
    @Override
    public XClientOps getSingleClientInterestedInEvent(WINDOW window, int event) {
        final List<XClientOps> list = getClientListInterestedInEvent(window, event);
        
        if (list.size() > 1) {
            throw new IllegalStateException();
        }
        
        return list.isEmpty() ? null : list.get(0);
    }


    @Override
    public int getAllEventMasks(WINDOW window) {
        
        Objects.requireNonNull(window);
        
        int mask = 0;
        
        for (Key key : eventToClients.keySet()) {
            if (key.window.equals(window)) {
                mask |= key.event;
            }
        }
        
        return mask;
    }

    @Override
    public int getYourEventMask(WINDOW window, XClientOps client) {

        Objects.requireNonNull(window);
        Objects.requireNonNull(client);
        
        int mask = 0;
        
        for (Map.Entry<Key, List<XClientOps>> entry : eventToClients.entrySet()) {
            
            final Key key = entry.getKey();
            
            if (key.window.equals(window) && entry.getValue().contains(client)) {
                mask |= key.event;
            }
        }
        
        return mask;
    }




    private static class Key {
        private final WINDOW window;
        private final int event;
        
        Key(WINDOW window, int event) {
            
            Objects.requireNonNull(window);
            
            this.window = window;
            this.event = event;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + event;
            result = prime * result + ((window == null) ? 0 : window.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Key other = (Key) obj;
            if (event != other.event)
                return false;
            if (window == null) {
                if (other.window != null)
                    return false;
            } else if (!window.equals(other.window))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return String.format("%08x/%08x", window.getValue(), event) ;
        }
    }
}
