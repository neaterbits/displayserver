package com.neaterbits.displayserver.xwindows.core.processing;

import org.junit.Test;

import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public final class XCorePixmapTest extends BaseXCorePixmapTest {

    @Test
    public void testPixmapOnRootWindow() {
        
        final Size size = new Size(150, 250);
        
        final PixmapState pixmapState = checkCreatePixmap(getRootDepthAsInt(), rootWindow.toDrawable(), size);
        
        checkFreePixmap(pixmapState);
    }

    @Test
    public void testPixmapOnWindow() {
        
        final WindowState windowState = checkCreateWindow(
                new Position(175, 275),
                new Size(350, 450));
        
        final Size pixmapSize = new Size(150, 250);
        
        final PixmapState pixmapState = checkCreatePixmap(getRootDepthAsInt(), rootWindow.toDrawable(), pixmapSize);
        
        checkFreePixmap(pixmapState);
        
        checkDestroyWindow(windowState);
    }
}
