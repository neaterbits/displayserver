package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;
import java.util.Collection;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.exception.AtomException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.events.PropertyNotify;
import com.neaterbits.displayserver.protocol.messages.replies.GetPropertyReply;
import com.neaterbits.displayserver.protocol.messages.replies.ListPropertiesReply;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeProperty;
import com.neaterbits.displayserver.protocol.messages.requests.DeleteProperty;
import com.neaterbits.displayserver.protocol.messages.requests.GetProperty;
import com.neaterbits.displayserver.protocol.messages.requests.ListProperties;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.server.XClientWindow;
import com.neaterbits.displayserver.server.XTimestampGenerator;
import com.neaterbits.displayserver.xwindows.model.Property;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCorePropertyMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XTimestampGenerator timestampGenerator;
    
    public XCorePropertyMessageProcessor(XWindowsServerProtocolLog protocolLog, XWindowsConstAccess<?> xWindows,
            XTimestampGenerator timestampGenerator) {
        
        super(protocolLog);

        this.xWindows = xWindows;
        this.timestampGenerator = timestampGenerator;
    }

    @Override
    protected int[] getOpCodes() {
        
        return new int [] {
                OpCodes.CHANGE_PROPERTY,
                OpCodes.DELETE_PROPERTY,
                OpCodes.GET_PROPERTY,
                OpCodes.LIST_PROPERTIES
        };
    }

    @Override
    protected void onMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException {

        switch (opcode) {
        
        case OpCodes.CHANGE_PROPERTY: {
            
            final ChangeProperty changeProperty = log(messageLength, opcode, sequenceNumber, ChangeProperty.decode(stream));

            final XWindow xWindow = xWindows.getClientOrRootWindow(changeProperty.getWindow());
            
            if (xWindow == null) {
                sendError(client, Errors.Window, sequenceNumber, changeProperty.getWindow().getValue(), opcode);
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
                            timestampGenerator.getTimestamp(),
                            PropertyNotify.NewValue);
                    
                    sendEvent(client, changeProperty.getWindow(), propertyNotify);
                }
                catch (MatchException ex) {
                    sendError(client, Errors.Match, sequenceNumber, 0, opcode);
                }
                catch (AtomException ex) {
                    sendError(client, Errors.Atom, sequenceNumber, ex.getAtom().getValue(), opcode);
                }
                catch (ValueException ex) {
                    sendError(client, Errors.Value, sequenceNumber, ex.getValue(), opcode);
                }
            }
            break;
        }
        
        case OpCodes.DELETE_PROPERTY: {
        
            final DeleteProperty deleteProperty = log(messageLength, opcode, sequenceNumber, DeleteProperty.decode(stream));
            
            final XWindow xWindow = xWindows.getClientWindow(deleteProperty.getWindow());
            
            if (xWindow == null) {
                sendError(client, Errors.Window, sequenceNumber, deleteProperty.getWindow().getValue(), opcode);
            }
            else {
                
                try {
                    xWindow.deleteProperty(deleteProperty.getProperty());
                    
                    final PropertyNotify propertyNotify = new PropertyNotify(
                            sequenceNumber,
                            deleteProperty.getWindow(),
                            deleteProperty.getProperty(),
                            timestampGenerator.getTimestamp(),
                            PropertyNotify.NewValue);
                    
                    sendEvent(client, deleteProperty.getWindow(), propertyNotify);
                }
                catch (AtomException ex) {
                    
                    // gnome-text-editor does not handle this error
                    
                    // serverToClient.sendError(client, Errors.Atom, sequenceNumber, ex.getAtom().getValue(), opcode);
                }
            }
            break;
        }
        
        case OpCodes.GET_PROPERTY: {
            
            final GetProperty getProperty = log(messageLength, opcode, sequenceNumber, GetProperty.decode(stream));
            
            final XWindow xWindow = xWindows.getClientOrRootWindow(getProperty.getWindow());
            
            if (xWindow == null) {
                sendError(client, Errors.Window, sequenceNumber, getProperty.getWindow().getValue(), opcode);
            }
            else {
                final Property property = xWindow.getProperty(getProperty.getProperty());
            
                if (property == null) {
                    sendReply(client, new GetPropertyReply(
                            sequenceNumber,
                            new CARD8((short)0),
                            ATOM.None,
                            0,
                            new byte[0]));
                }
                else if (!getProperty.getType().equals(property.getType()) && !ATOM.AnyPropertyType.equals(property.getType())) {
                    sendReply(client, new GetPropertyReply(
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
                        sendError(client, Errors.Value, sequenceNumber, getProperty.getLongOffset().getValue(), opcode);
                    }
                    else {
                        final long bytesAfter = A;
                        
                        final byte [] data = new byte[(int)L];
                        
                        System.arraycopy(property.getData(), (int)I, data, 0, (int)L);
                        
                        sendReply(client, new GetPropertyReply(
                                sequenceNumber,
                                property.getFormat(),
                                property.getType(),
                                (int)bytesAfter,
                                data));
                        
                        if (!xWindow.isRootWindow()) {
                            final XClientWindow xClientWindow = (XClientWindow)xWindow;
                            
                            if (xClientWindow.isCreatedBy(client) && bytesAfter == 0 && getProperty.getDelete().isSet()) {
        
                                xClientWindow.removeProperty(property.getProperty());
                                
                                final PropertyNotify propertyNotify = new PropertyNotify(
                                        sequenceNumber,
                                        getProperty.getWindow(),
                                        property.getProperty(),
                                        timestampGenerator.getTimestamp(),
                                        PropertyNotify.Deleted);
                                
                                sendEvent(client, getProperty.getWindow(), propertyNotify);
                            }
                        }
                    }
                }
            }
            break;
        }
        
        case OpCodes.LIST_PROPERTIES: {

            final ListProperties listProperties = log(messageLength, opcode, sequenceNumber, ListProperties.decode(stream));
            
            final XWindow xWindow = xWindows.getClientOrRootWindow(listProperties.getWindow());
            
            if (xWindow == null) {
                sendError(client, Errors.Window, sequenceNumber, listProperties.getWindow().getValue(), opcode);
            }
            else {
                final Collection<ATOM> atoms = xWindow.listPropertyAtoms();
                
                sendReply(client, new ListPropertiesReply(sequenceNumber, atoms.toArray(new ATOM[atoms.size()])));
            }
            break;
        }
        }
    }
}
