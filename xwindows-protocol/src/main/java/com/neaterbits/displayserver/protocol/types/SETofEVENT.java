package com.neaterbits.displayserver.protocol.types;

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
}
