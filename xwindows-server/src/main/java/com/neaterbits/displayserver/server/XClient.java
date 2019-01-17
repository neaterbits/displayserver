package com.neaterbits.displayserver.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.exception.FontException;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeGC;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.FreeGC;
import com.neaterbits.displayserver.protocol.messages.requests.GCAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.CloseFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.OpenFont;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryFont;
import com.neaterbits.displayserver.protocol.types.FONT;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.RESOURCE;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;

public class XClient extends XConnection implements XClientOps {
    
    private final Set<Integer> utilizedResourceIds;
    private final Map<GCONTEXT, XGC> gcs;

    private final Map<FONT, XFont> openFonts;
    
    public XClient(SocketChannel socketChannel, SelectionKey selectionKey, int connectionNo,
            NonBlockingChannelWriterLog log) {
        super(socketChannel, selectionKey, connectionNo, log);

        this.utilizedResourceIds = new HashSet<>();
        
        this.gcs = new HashMap<>();
        
        this.openFonts = new HashMap<>();
    }
    

    @Override
    public XFont getFont(FONT fontResource) throws FontException {
        
        Objects.requireNonNull(fontResource);
        
        final XFont font = openFonts.get(fontResource);
        
        if (font == null) {
            throw new FontException("No such font", fontResource);
        }
        
        return font;
    }

    @Override
    public void openFont(OpenFont openFont, XFont font) throws IDChoiceException {
        
        Objects.requireNonNull(openFont);
        Objects.requireNonNull(font);
        
        final FONT fontResource = openFont.getFid();
        
        checkAndAddResourceId(fontResource);
        
        if (openFonts.containsKey(fontResource)) {
            throw new IDChoiceException("Already open", fontResource);
        }
        
        openFonts.put(fontResource, font);
    }
    
    @Override
    public XFont closeFont(CloseFont closeFont) throws FontException {
        
        Objects.requireNonNull(closeFont);
        
        final FONT fontResource = closeFont.getFont();
        
        if (!openFonts.containsKey(fontResource)) {
            throw new FontException("Font not open", fontResource);
        }
        
        final XFont font = openFonts.remove(fontResource);

        checkAndRemoveResourceId(fontResource);
        
        return font;
    }
    
    @Override
    public XFont queryFont(QueryFont queryFont) throws FontException {
        
        final FONT fontResource = queryFont.getFont().toFontResource();
        
        XFont font = openFonts.get(fontResource);
        
        if (font == null) {
            final GCONTEXT gcResource = queryFont.getFont().toGCResource();
            
            final XGC gc;
            try {
                gc = getGC(gcResource);

                font = openFonts.get(gc.getAttributes().getFont());
                
            } catch (GContextException ex) {
                throw new FontException("No such font", queryFont.getFont());
            }
        }
        
        if (font == null) {
            throw new FontException("No such font", fontResource);
        }

        return font;
    }
    
    @Override
    public final void createGC(CreateGC createGC) throws IDChoiceException {
        
        if (gcs.containsKey(createGC.getCid())) {
            throw new IDChoiceException("ID already added", createGC.getCid());
        }
        
        final GCAttributes attributes = GCAttributes.DEFAULT_ATTRIBUTES.applyImmutably(createGC.getAttributes());

        addGC(createGC.getCid(), attributes);
    }
    
    private void addGC(GCONTEXT context, GCAttributes attributes) {
        
        Objects.requireNonNull(context);
        Objects.requireNonNull(attributes);

        if (gcs.containsKey(context)) {
            throw new IllegalStateException();
        }
        
        final XGC xgc = new XGC(attributes);
        
        gcs.put(context, xgc);
    }
    
    private void changeGC(GCONTEXT context, GCAttributes attributes) {
        
        Objects.requireNonNull(context);
        Objects.requireNonNull(attributes);
        
        final XGC existing = gcs.get(context);
        
        if (existing == null) {
            throw new IllegalStateException();
        }

        gcs.put(context, new XGC(existing.getAttributes().applyImmutably(attributes)));
    }

    private void removeGC(GCONTEXT context) {
        
        Objects.requireNonNull(context);
        
        gcs.remove(context);
    }

    @Override
    public void changeGC(ChangeGC changeGC) throws GContextException {
        
        final GCONTEXT gc = changeGC.getGc();
        
        changeGC(gc, changeGC.getAttributes());
    }
    
    @Override
    public XGC getGC(GCONTEXT gc) throws GContextException {

        Objects.requireNonNull(gc);
        
        final XGC xgc = gcs.get(gc);
        
        if (xgc == null) {
            throw new GContextException("No such GC", gc);
        }
        
        return xgc;
    }

    @Override
    public void freeGC(FreeGC freeGC) throws GContextException {
        removeGC(freeGC.getGContext());
    }
    
    
    @Override
    public void checkAndAddResourceId(RESOURCE resource) throws IDChoiceException {

        Objects.requireNonNull(resource);

        if (utilizedResourceIds.contains(resource.getValue())) {
            throw new IDChoiceException("Already utilized", resource);
        }
        
        utilizedResourceIds.add(resource.getValue());
    }
    
    @Override
    public void checkAndRemoveResourceId(RESOURCE resource) {
        
        Objects.requireNonNull(resource);
        
        utilizedResourceIds.remove(resource.getValue());
    }
}
