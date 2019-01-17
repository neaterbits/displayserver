package com.neaterbits.displayserver.xwindows.core.processing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;

import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.requests.CreatePixmap;
import com.neaterbits.displayserver.protocol.messages.requests.FreePixmap;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public abstract class BaseXCorePixmapTest extends BaseXCoreWindowTest {

    static class PixmapState {
        
        private final PIXMAP pixmapResource;
        private final OffscreenSurface surface;
        private final XLibRenderer renderer;

        public PixmapState(PIXMAP pixmapResource, OffscreenSurface surface, XLibRenderer renderer) {

            this.pixmapResource = pixmapResource;
            this.surface = surface;
            this.renderer = renderer;
        }
    }

    protected final PixmapState checkCreatePixmap(int depth, DRAWABLE drawable, Size size) {

        final OffscreenSurface surface = mock(OffscreenSurface.class);
        final XLibRenderer xlibRenderer = mock(XLibRenderer.class);

        when(displayArea.allocateOffscreenSurface(eq(size), same(rootPixelFormat))).thenReturn(surface);
        when(rendererFactory.createRenderer(same(surface), any()))
            .thenReturn(xlibRenderer);
        
        final PIXMAP pixmapResource = new PIXMAP(allocateResourceId());
        
        final CreatePixmap createPixmap = new CreatePixmap(
                new CARD8((byte)depth),
                pixmapResource,
                drawable,
                new CARD16(size.getWidth()),
                new CARD16(size.getHeight()));

        sendRequest(createPixmap);
        
        try {
            verify(client).checkAndAddResourceId(eq(pixmapResource));
        } catch (IDChoiceException ex) {
            throw new IllegalStateException(ex);
        }
        
        verify(displayArea).allocateOffscreenSurface(eq(size), same(rootPixelFormat));
        verify(rendererFactory).createRenderer(same(surface), same(rootPixelFormat));

        verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(surface, xlibRenderer);

        return new PixmapState(pixmapResource, surface, xlibRenderer);
    }
    
    protected final void checkFreePixmap(PixmapState pixmap) {

        final FreePixmap freePixmap = new FreePixmap(pixmap.pixmapResource);

        sendRequest(freePixmap);
        
        verify(client).checkAndRemoveResourceId(eq(pixmap.pixmapResource));
        verify(displayArea).freeOffscreenSurface(same(pixmap.surface));
        verify(pixmap.renderer).dispose();
        
        verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(pixmap.surface, pixmap.renderer);
    }
}
