package com.neaterbits.displayserver.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.enums.Mode;
import com.neaterbits.displayserver.protocol.exception.AtomException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;

final class XWindow {

    private final XClient createdBy;
    private final Window window;
    
    private final WINDOW windowResource;
    private final WINDOW rootWindow;
    private final WINDOW parentWindow;
    
    private final CARD16 borderWidth;
    private final CARD16 windowClass;
    
    private WindowAttributes currentWindowAttributes;
    
    private final Map<ATOM, Property> properties;

    // Root window
    XWindow(
            Window window,
            WINDOW windowResource,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes) {
        
        this(null, window, windowResource, WINDOW.None, WINDOW.None, borderWidth, windowClass, currentWindowAttributes, 0);
    }

    XWindow(
            XClient createdBy,
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes) {
        
        this(createdBy, window, windowResource, rootWindow, parentWindow, borderWidth, windowClass, currentWindowAttributes, 0);
        
        Objects.requireNonNull(createdBy);
        
        if (rootWindow.equals(WINDOW.None)) {
            throw new IllegalArgumentException();
        }
        
        if (parentWindow.equals(WINDOW.None)) {
            throw new IllegalArgumentException();
        }
    }

    XWindow(
            XClient createdBy,
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            int disambiguate) {
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(windowResource);
        Objects.requireNonNull(rootWindow);
        Objects.requireNonNull(parentWindow);
        Objects.requireNonNull(borderWidth);
        Objects.requireNonNull(windowClass);
        Objects.requireNonNull(currentWindowAttributes);
        
        this.createdBy = createdBy;
        this.window = window;
        this.windowResource = windowResource;
        this.rootWindow = rootWindow;
        this.parentWindow = parentWindow;
        this.borderWidth = borderWidth;
        this.windowClass = windowClass;
        this.currentWindowAttributes = currentWindowAttributes;
        
        this.properties = new HashMap<>();
    }
    
    boolean isRootWindow() {
        return parentWindow.equals(WINDOW.None);
    }
    
    boolean isCreatedBy(XClient client) {

        Objects.requireNonNull(client);

        return createdBy == client;
    }

    Window getWindow() {
        return window;
    }
    
    WINDOW getWINDOW() {
        return windowResource;
    }
    
    WINDOW getRootWINDOW() {
        return rootWindow;
    }
    
    WINDOW getParentWINDOW() {
        return parentWindow;
    }

    byte getDepth() {
        return (byte)window.getDepth();
    }

    short getX() {
        return (short)window.getPosition().getLeft();
    }
    
    short getY() {
        return (short)window.getPosition().getTop();
    }
    
    int getWidth() {
        return window.getSize().getWidth();
    }
    
    int getHeight() {
        return window.getSize().getHeight();
    }

    CARD16 getBorderWidth() {
        return borderWidth;
    }
    
    CARD16 getWindowClass() {
        return windowClass;
    }

    WindowAttributes getCurrentWindowAttributes() {
        return currentWindowAttributes;
    }
    
    void setCurrentWindowAttributes(WindowAttributes currentWindowAttributes) {
        this.currentWindowAttributes = currentWindowAttributes;
    }

    Property getProperty(ATOM property) {
        
        Objects.requireNonNull(property);
        
        return properties.get(property);
    }
    
    void changeProperty(BYTE mode, ATOM propertyAtom, ATOM type, CARD8 format, byte[] data) throws MatchException, AtomException, ValueException {
        
        final Property property = properties.get(propertyAtom);
        
        if (mode.getValue() == Mode.REPLACE || property == null) {
            properties.put(propertyAtom, new Property(propertyAtom, type, format, data));
        }
        else {
            switch (mode.getValue()) {
            case Mode.PREPEND:
                checkMatch(property, type, format);

                properties.put(propertyAtom, new Property(propertyAtom, type, format, merge(data, property.getData())));
                break;
                
            case Mode.APPEND:
                checkMatch(property, type, format);
    
                properties.put(propertyAtom, new Property(propertyAtom, type, format, merge(property.getData(), data)));
                break;
                
            default:
                throw new ValueException("Unknown mode type " + mode.getValue(), mode.getValue());
            }
        }
    }
    
    private static byte [] merge(byte [] array1, byte [] array2) {
        final byte [] result = Arrays.copyOf(array1, array1.length + array2.length);
        
        System.arraycopy(array2, 0, result, array1.length, array2.length);
    
        return result;
    }
    
    private void checkMatch(Property property, ATOM type, CARD8 format) throws MatchException {
        
        if (!property.getType().equals(type)) {
            throw new MatchException("Types do not match: " + property.getType() + "/" + type);
        }
        
        if (!property.getFormat().equals(format)) {
            throw new MatchException("Formats do not match: " + property.getFormat() + "/" + format);
        }
    }
    
    void deleteProperty(ATOM property) {
        
        Objects.requireNonNull(property);
        
        properties.remove(property);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        XWindow other = (XWindow) obj;
        if (window == null) {
            if (other.window != null)
                return false;
        } else if (!window.equals(other.window))
            return false;
        return true;
    }
}
