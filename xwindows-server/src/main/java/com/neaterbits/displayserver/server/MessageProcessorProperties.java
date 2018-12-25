package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.exception.AtomException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.messages.events.PropertyNotify;
import com.neaterbits.displayserver.protocol.messages.replies.GetPropertyReply;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeProperty;
import com.neaterbits.displayserver.protocol.messages.requests.DeleteProperty;
import com.neaterbits.displayserver.protocol.messages.requests.GetProperty;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;

final class MessageProcessorProperties {

    static void changeProperty(ChangeProperty changeProperty, int opcode, CARD16 sequenceNumber, TIMESTAMP timestamp, XClient client, XWindowsConstAccess xWindows, ServerToClient serverToClient) {
        final XWindow xWindow = xWindows.getClientWindow(changeProperty.getWindow());
        
        if (xWindow == null) {
            serverToClient.sendError(client, Errors.Window, sequenceNumber, changeProperty.getWindow().getValue(), opcode);
        }
        else {
            try {
                xWindow.changeProperty(
                        changeProperty.getMode(),
                        changeProperty.getProperty(),
                        changeProperty.getType(),
                        changeProperty.getFormat(),
                        changeProperty.getData());
                
                final PropertyNotify propertyNotify = new PropertyNotify(
                        sequenceNumber,
                        changeProperty.getWindow(),
                        changeProperty.getProperty(),
                        timestamp,
                        PropertyNotify.NewValue);
                
                serverToClient.sendEvent(client, changeProperty.getWindow(), propertyNotify);
            }
            catch (MatchException ex) {
                serverToClient.sendError(client, Errors.Match, sequenceNumber, 0, opcode);
            }
            catch (AtomException ex) {
                serverToClient.sendError(client, Errors.Atom, sequenceNumber, ex.getAtom().getValue(), opcode);
            }
            catch (ValueException ex) {
                serverToClient.sendError(client, Errors.Value, sequenceNumber, ex.getValue(), opcode);
            }
        }
    }
    
    static void deleteProperty(DeleteProperty deleteProperty, int opcode, CARD16 sequenceNumber, TIMESTAMP timestamp, XClient client, XWindowsConstAccess xWindows, ServerToClient serverToClient) {
        final XWindow xWindow = xWindows.getClientWindow(deleteProperty.getWindow());
        
        if (xWindow == null) {
            serverToClient.sendError(client, Errors.Window, sequenceNumber, deleteProperty.getWindow().getValue(), opcode);
        }
        else {
            
            try {
                xWindow.deleteProperty(deleteProperty.getProperty());
                
                final PropertyNotify propertyNotify = new PropertyNotify(
                        sequenceNumber,
                        deleteProperty.getWindow(),
                        deleteProperty.getProperty(),
                        timestamp,
                        PropertyNotify.NewValue);
                
                serverToClient.sendEvent(client, deleteProperty.getWindow(), propertyNotify);
            }
            catch (AtomException ex) {
                
                // gnome-text-editor does not handle this error
                
                // serverToClient.sendError(client, Errors.Atom, sequenceNumber, ex.getAtom().getValue(), opcode);
            }
        }
    }
    
    static void getProperty(GetProperty getProperty, int opcode, CARD16 sequenceNumber, TIMESTAMP timestamp, XClient client, XWindowsConstAccess xWindows, ServerToClient serverToClient) {
        final XWindow xWindow = xWindows.getClientOrRootWindow(getProperty.getWindow());
        
        if (xWindow == null) {
            serverToClient.sendError(client, Errors.Window, sequenceNumber, getProperty.getWindow().getValue(), opcode);
        }
        else {
            final Property property = xWindow.getProperty(getProperty.getProperty());
        
            if (property == null) {
                serverToClient.sendReply(client, new GetPropertyReply(
                        sequenceNumber,
                        new CARD8((short)0),
                        ATOM.None,
                        0,
                        new byte[0]));
            }
            else if (!getProperty.getType().equals(property.getType()) && !ATOM.AnyPropertyType.equals(property.getType())) {
                serverToClient.sendReply(client, new GetPropertyReply(
                        sequenceNumber,
                        property.getFormat(),
                        property.getType(),
                        property.getData().length,
                        new byte[0]));
            }
            else {
                final long N = property.getData().length;
                final long I = 4 * getProperty.getLongOffset().getValue();
                final long T = N - I;
                final long L = Math.min(T, 4 * getProperty.getLongLength().getValue());
                final long A = N - (I + L);

                if (L < 0) {
                    serverToClient.sendError(client, Errors.Value, sequenceNumber, getProperty.getLongOffset().getValue(), opcode);
                }
                else {
                    final long bytesAfter = A;
                    
                    final byte [] data = new byte[(int)L];
                    
                    System.arraycopy(property.getData(), (int)I, data, 0, (int)L);
                    
                    serverToClient.sendReply(client, new GetPropertyReply(
                            sequenceNumber,
                            property.getFormat(),
                            property.getType(),
                            (int)bytesAfter,
                            data));
                    
                    if (xWindow.isCreatedBy(client) && bytesAfter == 0 && getProperty.getDelete().isSet()) {

                        xWindow.removeProperty(property.getProperty());
                        
                        final PropertyNotify propertyNotify = new PropertyNotify(
                                sequenceNumber,
                                getProperty.getWindow(),
                                property.getProperty(),
                                timestamp,
                                PropertyNotify.Deleted);
                        
                        serverToClient.sendEvent(client, getProperty.getWindow(), propertyNotify);
                    }
                }
            }
        }
    }
    
}
