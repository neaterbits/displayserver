package com.neaterbits.displayserver.protocol.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SETofEVENT {
    
    public static final int KEY_PRESS           = 0x00000001;
    public static final int KEY_RELEASE         = 0x00000002;
    public static final int BUTTON_PRESS        = 0x00000004;
    public static final int BUTTON_RELEASE      = 0x00000008;
    public static final int ENTER_WINDOW        = 0x00000010;
    public static final int LEAVE_WINDOW        = 0x00000020;
    public static final int POINTER_MOTION      = 0x00000040;
    public static final int POINTER_MOTION_HINT = 0x00000080;
    public static final int BUTTON1_MOTION      = 0x00000100;
    public static final int BUTTON2_MOTION      = 0x00000200;
    public static final int BUTTON3_MOTION      = 0x00000400;
    public static final int BUTTON4_MOTION      = 0x00000800;
    public static final int BUTTON5_MOTION      = 0x00001000;
    public static final int BUTTON_MOTION       = 0x00002000;
    public static final int KEYMAP_STATE        = 0x00004000;
    public static final int EXPOSURE            = 0x00008000;
    public static final int VISIBILITY_CHANGE   = 0x00010000;
    public static final int STRUCTURE_NOTIFY    = 0x00020000;
    public static final int RESIZE_REDIRECT     = 0x00040000;
    public static final int SUBSTRUCTURE_NOTIFY = 0x00080000;
    public static final int SUBSTRUCTURE_REDIRECT = 0x00100000;
    public static final int FOCUS_CHANGE        = 0x00200000;
    public static final int PROPERTY_CHANGE     = 0x00400000;
    public static final int COLORMAP_CHANGE     = 0x00800000;
    public static final int OWNER_GRAB_BUTTON   = 0x01000000;
    public static final int UNUSED              = 0xFE000000;

    private static List<String> getEventStrings(int eventMask) {
        
        final List<String> strings = new ArrayList<>(32);
        
        addEventString(strings, "KeyPress",         eventMask, KEY_PRESS);
        addEventString(strings, "KeyRelease",       eventMask, KEY_RELEASE);
        addEventString(strings, "ButtonPress",      eventMask, BUTTON_PRESS);
        addEventString(strings, "ButtonRelease",    eventMask, BUTTON_RELEASE);
        addEventString(strings, "EnterWindow",      eventMask, ENTER_WINDOW);
        addEventString(strings, "LeaveWindow",      eventMask, LEAVE_WINDOW);
        addEventString(strings, "PointerMotion",    eventMask, POINTER_MOTION);
        addEventString(strings, "PointerMotionHint", eventMask, POINTER_MOTION_HINT);
        addEventString(strings, "Button1Motion",    eventMask, BUTTON1_MOTION);
        addEventString(strings, "Button2Motion",    eventMask, BUTTON2_MOTION);
        addEventString(strings, "Button3Motion",    eventMask, BUTTON3_MOTION);
        addEventString(strings, "Button4Motion",    eventMask, BUTTON4_MOTION);
        addEventString(strings, "Button5Motion",    eventMask, BUTTON5_MOTION);
        addEventString(strings, "ButtonMotion",     eventMask, BUTTON_MOTION);
        addEventString(strings, "KeymapState",      eventMask, KEYMAP_STATE);
        addEventString(strings, "Exposure",         eventMask, EXPOSURE);
        addEventString(strings, "VisibilityChange", eventMask, VISIBILITY_CHANGE);
        addEventString(strings, "StructureNotify",  eventMask, STRUCTURE_NOTIFY);
        addEventString(strings, "ResizeRedirect",   eventMask, RESIZE_REDIRECT);
        addEventString(strings, "SubstructureNotify",   eventMask, SUBSTRUCTURE_NOTIFY);
        addEventString(strings, "SubstructureRedirect", eventMask, SUBSTRUCTURE_REDIRECT);
        addEventString(strings, "FocusChange",      eventMask, FOCUS_CHANGE);
        addEventString(strings, "PropertyChange",   eventMask, PROPERTY_CHANGE);
        addEventString(strings, "ColormapChange",   eventMask, COLORMAP_CHANGE);
        addEventString(strings, "OwnerGrabButton",  eventMask, OWNER_GRAB_BUTTON);
        
        return strings;
    }
    
    public List<String> getEventStrings() {
        return getEventStrings(value);
    }
    
    private static void addEventString(List<String> strings, String string, int eventMask, int event) {
        
        Objects.requireNonNull(strings);
        Objects.requireNonNull(string);
        
        if ((eventMask & event) != 0) {
            strings.add(string);
        }
    }
    
    private final int value;

    public SETofEVENT(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%08x", value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
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
        SETofEVENT other = (SETofEVENT) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
