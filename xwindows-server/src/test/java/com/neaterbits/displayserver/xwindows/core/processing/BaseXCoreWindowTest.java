package com.neaterbits.displayserver.xwindows.core.processing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.XError;
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
        final Surface surface;
        final XLibRenderer renderer;

        WindowState(WINDOW windowResource, Window window, Surface surface, XLibRenderer renderer) {

            this.windowResource = windowResource;
            this.window = window;
            this.surface = surface;
            this.renderer = renderer;
        }
        
        private void verifyNoMoreInteractions() {
            Mockito.verifyNoMoreInteractions(surface, renderer);
        }
    }

    protected final void verifyNoMoreInteractions(WindowState windowState) {
        
        verifyNoMoreInteractions();
        
        Mockito.verifyNoMoreInteractions(windowState.surface, windowState.renderer);
    }

    protected final WindowState checkCreateWindow() {
        
        final Position position = new Position(150, 150);
        final Size size = new Size(350, 350);

        final WindowState window = checkCreateWindow(position, size);

        return window;
    }

    protected final void subscribeEvents(WINDOW window, int events) {
        changeWindowAttributes(window, b -> b.setEventMask(events), true);
    }

    protected final void changeWindowAttributes(WindowState windowState, Consumer<XWindowAttributesBuilder> builder) {
        changeWindowAttributes(windowState.windowResource, builder, true);
    }

    protected final XError changeWindowAttributesAndExpectError(WindowState windowState, Consumer<XWindowAttributesBuilder> builder) {

        changeWindowAttributes(windowState.windowResource, builder, false);
        
        final XError error = expectError();
        
        assertThat(error).isNotNull();
        
        verifyNoMoreInteractions(windowState);
        
        return error;
    }
    
    protected final void changeWindowAttributes(WINDOW window, Consumer<XWindowAttributesBuilder> builder, boolean verifyNoMoreInteractions) {
        final XWindowAttributesBuilder xWindowAttributesBuilder = new XWindowAttributesBuilder();
        
        builder.accept(xWindowAttributesBuilder);
        
        final XWindowAttributes windowAttributes = xWindowAttributesBuilder.build();
    
        final ChangeWindowAttributes changeWindowAttributes = new ChangeWindowAttributes(
                window,
                windowAttributes);
    
        sendRequest(changeWindowAttributes);
        
        if (verifyNoMoreInteractions) {
            verifyNoMoreInteractions();
        }
    }
    
    protected final WindowState checkCreateWindow(Position position, Size size) {
        return checkCreateWindow(position, size, 0, null, null, true);
    }

    protected final WindowState checkCreateWindow(
            Position position,
            Size size,
            int borderWidth,
            XWindowAttributes windowAttributes) {
        
        return checkCreateWindow(position, size, borderWidth, windowAttributes, null, true);
    }

    protected final WindowState checkCreateWindow(
            Position position,
            Size size,
            int borderWidth,
            XWindowAttributes windowAttributes,
            WINDOW window) {
        
        return checkCreateWindow(position, size, borderWidth, windowAttributes, window, true);
    }

    protected final WindowState checkCreateWindow(
            Position position,
            Size size,
            int borderWidth,
            XWindowAttributes windowAttributes,
            WINDOW wr,
            boolean verifyNoMoreInteractions) {

        final Surface surface = mock(Surface.class);
        final XLibRenderer xlibRenderer = mock(XLibRenderer.class);

        when(compositor.allocateSurfaceForClientWindow(isNotNull())).thenReturn(surface);
        when(displayArea.getPixelFormat()).thenReturn(rootPixelFormat);
        when(rendererFactory.createRenderer(isNotNull(), any()))
            .thenReturn(xlibRenderer);
        
        final WINDOW window = createWindow(position, size, borderWidth, windowAttributes, wr);
        
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
        
        if (verifyNoMoreInteractions) {
            verifyNoMoreInteractions();
        }
        
        Mockito.verifyNoMoreInteractions(surface, xlibRenderer);
        
        final Window w = surfaceWindow.getValue();
        
        assertThat(w.getPosition()).isEqualTo(position);
        assertThat(w.getSize()).isEqualTo(new Size(size.getWidth() + borderWidth * 2, size.getHeight() + borderWidth * 2));

        return new WindowState(window, w, surface, xlibRenderer);
    }
    
    protected final void checkDestroyWindow(WindowState window) {

        closeWindow(window.windowResource);
        
        verify(client).checkAndRemoveResourceId(eq(window.windowResource));
        verify(compositor).freeSurfaceForClientWindow(same(window.window), same(window.surface));
        verify(window.renderer).dispose();
        
        verifyNoMoreInteractions();
        window.verifyNoMoreInteractions();
    }
}
