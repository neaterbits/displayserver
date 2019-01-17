package com.neaterbits.displayserver.xwindows.core.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.xwindows.core.util.XWindowAttributesBuilder;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public abstract class BaseXCoreWindowTest extends BaseXCoreTest {

    static class WindowState {
        final WINDOW windowResource;
        private final Window window;
        private final Surface surface;
        private final XLibRenderer renderer;

        public WindowState(WINDOW windowResource, Window window, Surface surface, XLibRenderer renderer) {

            this.windowResource = windowResource;
            this.window = window;
            this.surface = surface;
            this.renderer = renderer;
        }
    }

    protected final WindowState checkCreateWindow() {
        
        final Position position = new Position(150, 150);
        final Size size = new Size(350, 350);

        final WindowState window = checkCreateWindow(position, size);

        return window;
    }

    protected final void subscribeEvents(WINDOW window, int events) {
        
        final XWindowAttributes windowAttributes = new XWindowAttributesBuilder()
                .setEventMask(events)
                .build();
        
        final ChangeWindowAttributes changeWindowAttributes = new ChangeWindowAttributes(window, windowAttributes);
        
        sendRequest(changeWindowAttributes);

        verifyNoMoreInteractions();
    }

    protected final WindowState checkCreateWindow(Position position, Size size) {

        final Surface surface = mock(Surface.class);
        final XLibRenderer xlibRenderer = mock(XLibRenderer.class);

        when(compositor.allocateSurfaceForClientWindow(isNotNull())).thenReturn(surface);
        when(rendererFactory.createRenderer(isNotNull(), any()))
            .thenReturn(xlibRenderer);
        
        final WINDOW window = createWindow(position, size);
        
        final ArgumentCaptor<Window> surfaceWindow = ArgumentCaptor.forClass(Window.class);
        
        try {
            verify(client).checkAndAddResourceId(eq(window.toDrawable()));
        } catch (IDChoiceException ex) {
            throw new IllegalStateException(ex);
        }
        
        verify(compositor).allocateSurfaceForClientWindow(surfaceWindow.capture());
        verify(displayArea).getPixelFormat();
        verify(rendererFactory).createRenderer(isNotNull(), same(rootPixelFormat));

        assertThat(window).isNotNull();
        
        verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(surface, xlibRenderer);
        
        final Window w = surfaceWindow.getValue();
        
        assertThat(w.getPosition()).isEqualTo(position);
        assertThat(w.getSize()).isEqualTo(size);

        return new WindowState(window, w, surface, xlibRenderer);
    }
    
    protected final void checkDestroyWindow(WindowState window) {

        closeWindow(window.windowResource);
        
        verify(client).checkAndRemoveResourceId(eq(window.windowResource));
        verify(compositor).freeSurfaceForClientWindow(same(window.window), same(window.surface));
        verify(window.renderer).dispose();
        
        verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(window.surface, window.renderer);
    }
}
