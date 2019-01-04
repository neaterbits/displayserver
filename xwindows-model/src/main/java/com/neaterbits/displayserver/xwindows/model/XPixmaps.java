package com.neaterbits.displayserver.xwindows.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class XPixmaps extends XResources<XPixmap> implements XPixmapsConstAccess {

    private final Map<PIXMAP, XPixmap> drawableToXPixmap;
    private final Map<PIXMAP, DRAWABLE> pixmapToOwnerDrawable;

    public XPixmaps() {
        this.drawableToXPixmap = new HashMap<>();
        this.pixmapToOwnerDrawable = new HashMap<>();
    }

    @Override
    public Collection<XPixmap> getResources() {
        return Collections.unmodifiableCollection(drawableToXPixmap.values());
    }

    @Override
    public XPixmap getPixmap(PIXMAP pixmap) {
        
        Objects.requireNonNull(pixmap);
        
        return drawableToXPixmap.get(pixmap);
    }
    
    void addPixmap(PIXMAP resource, DRAWABLE drawable, XPixmap xPixmap) {
        
        Objects.requireNonNull(resource);
        Objects.requireNonNull(drawable);
        Objects.requireNonNull(xPixmap);

        drawableToXPixmap.put(resource, xPixmap);
        
        pixmapToOwnerDrawable.put(resource, drawable);
        
    }

    XPixmap removePixmap(PIXMAP resource) {
        
        Objects.requireNonNull(resource);
        
        final XPixmap xPixmap = drawableToXPixmap.remove(resource);
        
        pixmapToOwnerDrawable.remove(resource);

        return xPixmap;
    }

    DRAWABLE getOwnerDrawable(PIXMAP pixmapDrawable) {
        return pixmapToOwnerDrawable.get(pixmapDrawable);        
    }
}
