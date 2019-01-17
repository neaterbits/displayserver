package com.neaterbits.displayserver.xwindows.processing;

import com.neaterbits.displayserver.protocol.exception.FontException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.FreeGC;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.CloseFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.OpenFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryFont;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.RESOURCE;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.model.XGC;

public interface XClientOps extends XConnectionOps {

    void checkAndAddResourceId(RESOURCE resource) throws IDChoiceException;

    void checkAndRemoveResourceId(RESOURCE resource);
    
    void openFont(OpenFont openFont, XFont font) throws IDChoiceException;

    XFont getFont(FONT font) throws FontException;
    
    XFont closeFont(CloseFont closeFont) throws FontException;

    XFont queryFont(QueryFont queryFont) throws FontException;

    void createGC(CreateGC createGC) throws IDChoiceException;
    
    void changeGC(ChangeGC changeGC) throws GContextException;

    XGC getGC(GCONTEXT gc) throws GContextException;
    
    void freeGC(FreeGC freeGC) throws GContextException;
}
