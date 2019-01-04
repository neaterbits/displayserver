package com.neaterbits.displayserver.xwindows.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;

public final class XPixmaps extends XResources<XPixmap> implements XPixmapsConstAccess {

    private final Map<DRAWABLE, XPixmap> drawableToXPixmap;
    private final Map<DRAWABLE, DRAWABLE> pixmapToOwnerDrawable;

    public XPixmaps() {
        this.drawableToXPixmap = new HashMap<>();
        this.pixmapToOwnerDrawable = new HashMap<>();
    }

    @Override
    public Collection<XPixmap> getResources() {
        return Collections.unmodifiableCollection(drawableToXPixmap.values());
    }

    @Override
    public XPixmap getPixmap(DRAWABLE drawable) {
        
        Objects.requireNonNull(drawable);
        
        return drawableToXPixmap.get(drawable);
    }
    
    void addPixmap(PIXMAP resource, DRAWABLE drawable, XPixmap xPixmap) {
        
        Objects.requireNonNull(resource);
        Objects.requireNonNull(drawable);
        Objects.requireNonNull(xPixmap);

        final DRAWABLE pixmapDrawable = resource.toDrawable();
        
        drawableToXPixmap.put(pixmapDrawable, xPixmap);
        
        pixmapToOwnerDrawable.put(pixmapDrawable, drawable);
        
    }

    XPixmap removePixmap(PIXMAP resource) {
        
        Objects.requireNonNull(resource);
        
        final DRAWABLE pixmapDrawable = resource.toDrawable();

        final XPixmap xPixmap = drawableToXPixmap.remove(pixmapDrawable);
        
        pixmapToOwnerDrawable.remove(pixmapDrawable);

        return xPixmap;
    }

    DRAWABLE getOwnerDrawable(DRAWABLE pixmapDrawable) {
        return pixmapToOwnerDrawable.get(pixmapDrawable);        
    }
}
