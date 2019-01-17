package com.neaterbits.displayserver.xwindows.core.processing;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;


public final class XCoreWindowTest extends BaseXCoreTest {

    @Test
    public void testOpenAndCloseWindow() throws IDChoiceException {

        final Surface surface = mock(Surface.class);
        final XLibRenderer xlibRenderer = mock(XLibRenderer.class);

        when(compositor.allocateSurfaceForClientWindow(isNotNull())).thenReturn(surface);
        when(rendererFactory.createRenderer(isNotNull(), any()))
            .thenReturn(xlibRenderer);
        
        final Position position = new Position(150, 150);
        final Size size = new Size(350, 350);
        
        final WINDOW window = createWindow(position, size);
        
        final ArgumentCaptor<Window> surfaceWindow = ArgumentCaptor.forClass(Window.class);
        
        verify(client).checkAndAddResourceId(eq(window.toDrawable()));
        verify(compositor).allocateSurfaceForClientWindow(surfaceWindow.capture());
        verify(displayArea).getPixelFormat();
        verify(rendererFactory).createRenderer(isNotNull(), same(rootPixelFormat));

        assertThat(window).isNotNull();
        
        verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(surface, xlibRenderer);
        
        final Window w = surfaceWindow.getValue();
        
        assertThat(w.getPosition()).isEqualTo(position);
        assertThat(w.getSize()).isEqualTo(size);
        
        closeWindow(window);
        
        verify(client).checkAndRemoveResourceId(eq(window));
        verify(compositor).freeSurfaceForClientWindow(same(surfaceWindow.getValue()), same(surface));
        verify(xlibRenderer).dispose();
        
        verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(surface, xlibRenderer);
    }
}
