package com.neaterbits.displayserver.xwindows.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.protocol.enums.Mode;
import com.neaterbits.displayserver.protocol.exception.AtomException;
import com.neaterbits.displayserver.protocol.exception.MatchException;
import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public class XWindow extends XDrawable {

    private final Window window;
    
    private final WINDOW windowResource;
    private final WINDOW rootWindow;
    private final WINDOW parentWindow;
    
    private CARD16 borderWidth;
    private final CARD16 windowClass;
    
    private WindowAttributes currentWindowAttributes;
    
    private final BufferOperations bufferOperations;
    
    private final Map<ATOM, Property> properties;

    // Root window
    public XWindow(
            Window window,
            WINDOW windowResource,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer,
            BufferOperations bufferOperations) {
        
        this(window, windowResource, WINDOW.None, WINDOW.None, visual, borderWidth, windowClass, currentWindowAttributes, renderer, bufferOperations, 0);
    }

    protected XWindow(
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer,
            BufferOperations bufferOperations) {
        
        this(window, windowResource, rootWindow, parentWindow, visual, borderWidth, windowClass, currentWindowAttributes, renderer, bufferOperations, 0);
        
        if (rootWindow.equals(WINDOW.None)) {
            throw new IllegalArgumentException();
        }
        
        if (parentWindow.equals(WINDOW.None)) {
            throw new IllegalArgumentException();
        }
    }

    private XWindow(
            Window window,
            WINDOW windowResource, WINDOW rootWindow, WINDOW parentWindow,
            VISUALID visual,
            CARD16 borderWidth,
            CARD16 windowClass,
            WindowAttributes currentWindowAttributes,
            XLibRenderer renderer,
            BufferOperations bufferOperations,
            int disambiguate) {
        
        super(visual, renderer);
        
        Objects.requireNonNull(window);
        Objects.requireNonNull(windowResource);
        Objects.requireNonNull(rootWindow);
        Objects.requireNonNull(parentWindow);
        Objects.requireNonNull(visual);
        Objects.requireNonNull(borderWidth);
        Objects.requireNonNull(windowClass);
        Objects.requireNonNull(currentWindowAttributes);
        
        this.window = window;
        this.windowResource = windowResource;
        this.rootWindow = rootWindow;
        this.parentWindow = parentWindow;
        this.borderWidth = borderWidth;
        this.windowClass = windowClass;
        this.currentWindowAttributes = currentWindowAttributes;

        Objects.requireNonNull(bufferOperations);
        
        this.bufferOperations = bufferOperations;

        this.properties = new HashMap<>();
    }
    
    @Override
    public BufferOperations getBufferOperations() {
        return bufferOperations;
    }

    public final boolean isRootWindow() {
        return parentWindow.equals(WINDOW.None);
    }

    public final Window getWindow() {
        return window;
    }
    
    public final WINDOW getWINDOW() {
        return windowResource;
    }
    
    public final WINDOW getRootWINDOW() {
        
        if (isRootWindow()) {
            throw new IllegalStateException();
        }
        
        return rootWindow;
    }
    
    public final WINDOW getParentWINDOW() {
        return parentWindow;
    }

    public final byte getDepth() {
        return (byte)window.getDepth();
    }

    public final short getX() {
        return (short)window.getPosition().getLeft();
    }
    
    public final short getY() {
        return (short)window.getPosition().getTop();
    }
    
    public final int getWidth() {
        return window.getSize().getWidth();
    }
    
    public final int getHeight() {
        return window.getSize().getHeight();
    }

    public final CARD16 getBorderWidth() {
        return borderWidth;
    }
    
    public void setBorderWidth(CARD16 borderWidth) {
        
        Objects.requireNonNull(borderWidth);
        
        this.borderWidth = borderWidth;
    }

    public final CARD16 getWindowClass() {
        return windowClass;
    }

    public final WindowAttributes getCurrentWindowAttributes() {
        return currentWindowAttributes;
    }
    
    public final void setCurrentWindowAttributes(WindowAttributes currentWindowAttributes) {
        
        Objects.requireNonNull(currentWindowAttributes);
        
        this.currentWindowAttributes = currentWindowAttributes;
    }

    public final Property getProperty(ATOM property) {
        
        Objects.requireNonNull(property);
        
        return properties.get(property);
    }
    
    public final Collection<ATOM> listPropertyAtoms() {
        return Collections.unmodifiableCollection(properties.keySet());
    }
    
    public final void changeProperty(BYTE mode, ATOM propertyAtom, ATOM type, CARD8 format, byte[] data) throws MatchException, AtomException, ValueException {
        
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
    
    public final void deleteProperty(ATOM propertyAtom) throws AtomException {

        final Property property = removeProperty(propertyAtom);
        
        if (property == null) {
            throw new AtomException("No such property", propertyAtom);
        }
    }

    public final Property removeProperty(ATOM propertyAtom) {
        return properties.remove(propertyAtom);
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
