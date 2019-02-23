package com.neaterbits.displayserver.xwindows.core.processing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;

import com.neaterbits.displayserver.protocol.enums.BackingStore;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.events.Expose;
import com.neaterbits.displayserver.protocol.messages.events.MapNotify;
import com.neaterbits.displayserver.protocol.messages.events.MapRequest;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.ClearArea;
import com.neaterbits.displayserver.protocol.messages.requests.MapSubwindows;
import com.neaterbits.displayserver.protocol.messages.requests.MapWindow;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.protocol.util.XWindowAttributesBuilder;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public class XCoreWindowMapWindowTest extends BaseXCorePixmapTest {

    private void setOverrideRedirect(WINDOW window, BOOL value) {
        
        final XWindowAttributes windowAttributes = new XWindowAttributesBuilder()
                .setOverrideRedirect(value)
                .build();
        
        final ChangeWindowAttributes changeWindowAttributes = new ChangeWindowAttributes(window, windowAttributes);
        
        sendRequest(changeWindowAttributes);

        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindow() throws IDChoiceException {

        final WindowState window = checkCreateWindow();

        final MapWindow mapWindow = new MapWindow(window.windowResource);
    
        sendRequest(mapWindow);

        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowSubstructureRedirect() {

        final WindowState window = checkCreateWindow();

        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_REDIRECT);

        subscribeEvents(window.windowResource, SETofEVENT.STRUCTURE_NOTIFY);
        
        MapWindow mapWindow = new MapWindow(window.windowResource);
    
        whenEvent(MapRequest.class);
        
        sendRequest(mapWindow);

        final MapRequest mapRequest = expectEvent(MapRequest.class);
        
        assertThat(mapRequest).isNotNull();
    
        assertThat(mapRequest.getParent()).isEqualTo(rootWindow);
        assertThat(mapRequest.getWindow()).isEqualTo(window.windowResource);
        
        verifyNoMoreInteractions();

        Mockito.reset(client);
        
        // Map window again to see that is mapped
        subscribeEvents(rootWindow, 0);
     
        mapWindow = new MapWindow(window.windowResource);
        
        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);
        
        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.False);

        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowSubstructureRedirectWhenOverride() {

        final WindowState window = checkCreateWindow();

        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_REDIRECT);
        subscribeEvents(window.windowResource, SETofEVENT.STRUCTURE_NOTIFY);
        
        setOverrideRedirect(window.windowResource, BOOL.True);
        
        MapWindow mapWindow = new MapWindow(window.windowResource);
    
        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);

        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.True);

        verifyNoMoreInteractions();
        
        // Map once again, should cause no events since already mapped
        
        Mockito.reset(client);

        mapWindow = new MapWindow(window.windowResource);

        sendRequest(mapWindow);
        
        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowSubstructureNotify() {

        final WindowState window = checkCreateWindow();

        subscribeEvents(rootWindow, SETofEVENT.SUBSTRUCTURE_NOTIFY);

        MapWindow mapWindow = new MapWindow(window.windowResource);

        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);
        
        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(rootWindow);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.False);

        verifyNoMoreInteractions();
        
        // Map once again, should cause no events since already mapped
        
        Mockito.reset(client);

        mapWindow = new MapWindow(window.windowResource);

        sendRequest(mapWindow);
        
        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowNotify() {
        
        final WindowState window = checkCreateWindow();

        subscribeEvents(window.windowResource, SETofEVENT.STRUCTURE_NOTIFY);

        MapWindow mapWindow = new MapWindow(window.windowResource);

        whenEvent(MapNotify.class);
        
        sendRequest(mapWindow);
        
        final MapNotify mapNotify = expectEvent(MapNotify.class);
        
        assertThat(mapNotify).isNotNull();
        assertThat(mapNotify.getEvent()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getWindow()).isEqualTo(window.windowResource);
        assertThat(mapNotify.getOverrideRedirect()).isEqualTo(BOOL.False);

        verifyNoMoreInteractions();
        
        // Map once again, should cause no events since already mapped
        
        Mockito.reset(client);

        mapWindow = new MapWindow(window.windowResource);

        sendRequest(mapWindow);
        
        verifyNoMoreInteractions();
    }

    @Test
    public void testMapWindowBackingStoreSubscribeInCreateWindow() {
        checkMapWindowBackingStore(BackingStore.NotUseful, true);
    }

    @Test
    public void testMapWindowBackingStoreNotUseful() {
        checkMapWindowBackingStore(BackingStore.NotUseful);
    }

    @Test
    public void testMapWindowBackingStoreWhenMapped() {
        checkMapWindowBackingStore(BackingStore.WhenMapped);
    }

    @Test
    public void testMapWindowBackingStoreAlways() {
        checkMapWindowBackingStore(BackingStore.Always);
    }
    
    @Test
    public void testMapSubwindows() {

        final Position position = new Position(150, 250);
        final Size size = new Size(450, 350);
        
        
        final WindowState window = checkCreateWindow(position, size);

        verifyNoMoreInteractions(window);

        Mockito.reset(compositor);
        Mockito.reset(displayArea);
        Mockito.reset(rendererFactory);
        
        final Position subPosition = new Position(15, 25);
        final Size subSize = new Size(45, 35);
        
        final WindowState subWindow = checkCreateWindow(subPosition, subSize, 0, null, window.windowResource);

        subscribeEvents(subWindow.windowResource, SETofEVENT.EXPOSURE);

        MapSubwindows mapSubwindows = new MapSubwindows(window.windowResource);
        
        whenEvent(Expose.class);

        sendRequest(mapSubwindows);

        final Expose expose = expectEvent(Expose.class);

        assertThat(expose).isNotNull();
        assertThat(expose.getWindow()).isEqualTo(subWindow.windowResource);

        assertThat(expose.getX().getValue()).isEqualTo(0);
        assertThat(expose.getY().getValue()).isEqualTo(0);
        assertThat(expose.getWidth().getValue()).isEqualTo(45);
        assertThat(expose.getHeight().getValue()).isEqualTo(35);
        
        assertThat(expose.getCount().getValue()).isEqualTo(0);
        
        verifyNoMoreInteractions(subWindow);
    }
    
    
    private void checkMapWindowBackingStore(BYTE backingStore) {
        checkMapWindowBackingStore(backingStore, false);
    }
    
    private void checkMapWindowBackingStore(BYTE backingStore, boolean subscribeInCreateWindow) {
        
        final Position position = new Position(150, 250);
        final Size size = new Size(450, 350);
        
        final XWindowAttributesBuilder windowAttributesBuilder = new XWindowAttributesBuilder();
        
        windowAttributesBuilder.setBackingStore(backingStore);
        
        if (subscribeInCreateWindow) {
            windowAttributesBuilder.setEventMask(SETofEVENT.EXPOSURE);
        }
                
        final XWindowAttributes windowAttributes = windowAttributesBuilder.build();
        
        final WindowState window = checkCreateWindow(position, size, 0, windowAttributes);

        if (!subscribeInCreateWindow) {
            subscribeEvents(window.windowResource, SETofEVENT.EXPOSURE);
        }

        MapWindow mapWindow = new MapWindow(window.windowResource);
        
        whenEvent(Expose.class);

        sendRequest(mapWindow);

        final Expose expose = expectEvent(Expose.class);

        assertThat(expose).isNotNull();
        assertThat(expose.getWindow()).isEqualTo(window.windowResource);

        assertThat(expose.getX().getValue()).isEqualTo(0);
        assertThat(expose.getY().getValue()).isEqualTo(0);
        assertThat(expose.getWidth().getValue()).isEqualTo(450);
        assertThat(expose.getHeight().getValue()).isEqualTo(350);
        
        assertThat(expose.getCount().getValue()).isEqualTo(0);
        
        verifyNoMoreInteractions(window);
    }
    
    
    
    @Test
    public void testWindowBackgroundPixel() {
        
        final Position position = new Position(250, 150);
        final Size size = new Size(450, 350);
        
        XWindowAttributes windowAttributes = new XWindowAttributesBuilder()
                .setBackgroundPixel(0x102030)
                .build();
        
        final WindowState window = checkCreateWindow(position, size, 0, windowAttributes);
        
        final MapWindow mapWindow = new MapWindow(window.windowResource);
        
        Mockito.reset(displayArea);

        when(displayArea.getPixelFormat()).thenReturn(rootPixelFormat);
        
        sendRequest(mapWindow);

        verify(displayArea).getPixelFormat();
        verify(window.renderer).fillRectangle(
                eq(0),
                eq(0),
                eq(size.getWidth()),
                eq(size.getHeight()),
                eq(0x10),
                eq(0x20),
                eq(0x30));
        
        verify(window.renderer).flush();
        
        verifyNoMoreInteractions(window);
        
        final PixmapState pixmap = checkCreatePixmap(
                rootPixelFormat.getDepth(),
                window.windowResource.toDrawable(),
                new Size(100, 100));
        
        // Change to pixmap but pixel has precedence

        changeWindowAttributes(window, b -> b.setBackgroundPixmap(pixmap.pixmapResource));

        pixmap.verifyNoMoreInteractions();

        Mockito.reset(displayArea);
        Mockito.reset(window.renderer);
        
        final ClearArea clearArea = new ClearArea(
                BOOL.False,
                window.windowResource,
                new INT16((short)0),
                new INT16((short)0),
                new CARD16(0),
                new CARD16(0));
        
        when(displayArea.getPixelFormat()).thenReturn(rootPixelFormat);

        sendRequest(clearArea);

        // Should still render background pixel

        verify(displayArea).getPixelFormat();
        verify(window.renderer).fillRectangle(
                eq(0),
                eq(0),
                eq(size.getWidth()),
                eq(size.getHeight()),
                eq(0x10),
                eq(0x20),
                eq(0x30));

        verify(window.renderer).flush();

        verifyNoMoreInteractions(window);
        Mockito.verifyNoMoreInteractions(pixmap.surface, pixmap.renderer);
    }

    @Test
    public void testWindowBackgroundPixmap() {

        final Size pixmapSize = new Size(100, 120);
        
        final PixmapState pixmap = checkCreatePixmap(
                rootPixelFormat.getDepth(),
                rootWindow.toDrawable(),
                pixmapSize);

        Mockito.reset(rendererFactory);
        
        final Position position = new Position(450, 350);
        final Size size = new Size(250, 150);
        
        XWindowAttributes windowAttributes = new XWindowAttributesBuilder()
                .setBackgroundPixmap(pixmap.pixmapResource)
                .build();
        
        final WindowState window = checkCreateWindow(position, size, 0, windowAttributes);
        
        final MapWindow mapWindow = new MapWindow(window.windowResource);

        when(pixmap.surface.getWidth()).thenReturn(pixmapSize.getWidth());
        when(pixmap.surface.getHeight()).thenReturn(pixmapSize.getHeight());
        
        sendRequest(mapWindow);

        verify(pixmap.surface).getWidth();
        verify(pixmap.surface).getHeight();
        
        checkRenderPixmapBackground(window, pixmap);
        
        verifyNoMoreInteractions(window);
        
        // Try to render by ClearArea too
        
        Mockito.reset(window.surface);

        final ClearArea clearArea = new ClearArea(
                BOOL.False,
                window.windowResource,
                new INT16((short)0),
                new INT16((short)0),
                new CARD16(0),
                new CARD16(0));

        sendRequest(clearArea);

        checkRenderPixmapBackground(window, pixmap);
        
        verifyNoMoreInteractions(window);
    }
    
    private void checkRenderPixmapBackground(WindowState window, PixmapState pixmap) {
        
        verify(window.surface).copyArea(
                same(pixmap.surface),
                eq(0), eq(0),
                eq(0), eq(0),
                eq(100), eq(120));

        verify(window.surface).copyArea(
                same(pixmap.surface),
                eq(0), eq(0),
                eq(100), eq(0),
                eq(100), eq(120));

        verify(window.surface).copyArea(
                same(pixmap.surface),
                eq(0), eq(0),
                eq(200), eq(0),
                eq(50), eq(120));

        verify(window.surface).copyArea(
                same(pixmap.surface),
                eq(0), eq(0),
                eq(0), eq(120),
                eq(100), eq(30));

        verify(window.surface).copyArea(
                same(pixmap.surface),
                eq(0), eq(0),
                eq(100), eq(120),
                eq(100), eq(30));

        verify(window.surface).copyArea(
                same(pixmap.surface),
                eq(0), eq(0),
                eq(200), eq(120),
                eq(50), eq(30));
    }
}
