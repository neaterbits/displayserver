package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class Errors {

    private static BYTE make(int error) {
        return new BYTE((byte)error);
    }

    public static final int REQUEST  = 1;
    public static final int VALUE    = 2;
    public static final int WINDOW   = 3;
    public static final int PIXMAP   = 4;
    public static final int ATOM     = 5;
    public static final int CURSOR   = 6;
    public static final int FONT     = 7;
    public static final int MATCH    = 8;
    public static final int DRAWABLE = 9;
    public static final int ACCESS   = 10;
    public static final int ALLOC    = 11;
    public static final int COLORMAP = 12;
    public static final int GCONTEXT = 13;
    public static final int IDCHOICE = 14;
    public static final int NAME     = 15;
    public static final int LENGTH   = 16;
    public static final int IMPLEMENTATION = 17;

    public static final BYTE Request  = make(REQUEST);
    public static final BYTE Value    = make(VALUE);
    public static final BYTE Window   = make(WINDOW);
    public static final BYTE Pixmap   = make(PIXMAP);
    public static final BYTE Atom     = make(ATOM);
    public static final BYTE Cursor   = make(CURSOR);
    public static final BYTE Font     = make(FONT);
    public static final BYTE Match    = make(MATCH);
    public static final BYTE Drawable = make(DRAWABLE);
    public static final BYTE Acess    = make(ACCESS);
    public static final BYTE Alloc    = make(ALLOC);
    public static final BYTE Colormap = make(COLORMAP);
    public static final BYTE GContext = make(GCONTEXT);
    public static final BYTE IDChoice = make(IDCHOICE);
    public static final BYTE Name     = make(NAME);
    public static final BYTE Length   = make(LENGTH);
    public static final BYTE Implementation = make(IMPLEMENTATION);
    
    public static String name(BYTE errorCode) {

        final String name;
        
        switch (errorCode.getValue()) {
        
        case REQUEST:   name = "Request"; break;
        case VALUE:     name = "Value"; break;
        case WINDOW:    name = "Window"; break;
        case PIXMAP:    name = "Pixmap"; break;
        case ATOM:      name = "Atom"; break;
        case CURSOR:    name = "Cursor"; break;
        case FONT:      name = "Font"; break;
        case MATCH:     name = "Match"; break;
        case DRAWABLE:  name = "Drawable"; break;
        case ACCESS:    name = "Access"; break;
        case ALLOC:     name = "Alloc"; break;
        case COLORMAP:  name = "Colormap"; break;
        case GCONTEXT:  name = "GContext"; break;
        case IDCHOICE:  name = "IDChoice"; break;
        case NAME:      name = "Name"; break;
        case LENGTH:    name = "Length"; break;
        case IMPLEMENTATION: name = "Implementation"; break;
        
        default:
            throw new UnsupportedOperationException("Unknown error code " + errorCode.getValue());
        }
    
        return name;
    }
}
