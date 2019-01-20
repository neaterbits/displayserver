package com.neaterbits.displayserver.xwindows.core.processing;

import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

import com.neaterbits.displayserver.protocol.messages.replies.GetGeometryReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetGeometry;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;

public class XCoreWindowGetGeometryTest extends BaseXCorePixmapTest {

    @Test
    public void testGetRootGeometry() {
        
        sendRequest(new GetGeometry(rootWindow.toDrawable()));
        
        GetGeometryReply reply = expectReply(GetGeometryReply.class);
        
        assertThat((int)reply.getDepth().getValue()).isEqualTo(getRootDepthAsInt());
        assertThat(reply.getRoot()).isEqualTo(rootWindow);
        assertThat(reply.getWidth().getValue()).isEqualTo(getRootWidth());
        assertThat(reply.getHeight().getValue()).isEqualTo(getRootHeight());
        assertThat((int)reply.getX().getValue()).isEqualTo(0);
        assertThat((int)reply.getY().getValue()).isEqualTo(0);
        assertThat(reply.getBorderWidth().getValue()).isEqualTo(0);
    }

    @Test
    public void testGetWindowGeometry() {
        
        final Position position = new Position(150, 250);
        final Size size = new Size(350, 450);

        final WindowState window = checkCreateWindow(position, size);
        
        Mockito.reset(displayArea);
        
        Mockito.when(displayArea.getDepth()).thenReturn(getRootDepthAsInt());

        sendRequest(new GetGeometry(window.windowResource.toDrawable()));
        
        GetGeometryReply reply = expectReply(GetGeometryReply.class);
        
        Mockito.verify(displayArea).getDepth();

        assertThat((int)reply.getDepth().getValue()).isEqualTo(getRootDepthAsInt());
        assertThat(reply.getRoot()).isEqualTo(rootWindow);
        assertThat(reply.getWidth().getValue()).isEqualTo(size.getWidth());
        assertThat(reply.getHeight().getValue()).isEqualTo(size.getHeight());
        assertThat((int)reply.getX().getValue()).isEqualTo(position.getLeft());
        assertThat((int)reply.getY().getValue()).isEqualTo(position.getTop());
        assertThat(reply.getBorderWidth().getValue()).isEqualTo(0);
        
        verifyNoMoreInteractions(window);
    }

    @Test
    public void testGetSubWindowGeometry() {
        
        final Position position = new Position(150, 250);
        final Size size = new Size(350, 450);

        final WindowState window = checkCreateWindow(position, size);

        final Position subPosition = new Position(50, 120);
        final Size subSize = new Size(250, 350);

        Mockito.reset(compositor);
        Mockito.reset(displayArea);
        Mockito.reset(rendererFactory);
        
        final WindowState subWindow = checkCreateWindow(subPosition, subSize, 0, null, window.windowResource);

        Mockito.when(displayArea.getDepth()).thenReturn(getRootDepthAsInt());

        sendRequest(new GetGeometry(subWindow.windowResource.toDrawable()));
        
        GetGeometryReply reply = expectReply(GetGeometryReply.class);

        Mockito.verify(displayArea).getDepth();

        assertThat((int)reply.getDepth().getValue()).isEqualTo(getRootDepthAsInt());
        assertThat(reply.getRoot()).isEqualTo(rootWindow);
        assertThat(reply.getWidth().getValue()).isEqualTo(subSize.getWidth());
        assertThat(reply.getHeight().getValue()).isEqualTo(subSize.getHeight());
        assertThat((int)reply.getX().getValue()).isEqualTo(subPosition.getLeft());
        assertThat((int)reply.getY().getValue()).isEqualTo(subPosition.getTop());
        assertThat(reply.getBorderWidth().getValue()).isEqualTo(0);
    }

    @Test
    public void testGetPixmapGeometry() {
        
        final Size size = new Size(350, 450);

        final PixmapState pixmap = checkCreatePixmap(getRootDepthAsInt(), rootWindow.toDrawable(), size);
        
        Mockito.when(pixmap.surface.getDepth()).thenReturn(getRootDepthAsInt());
        Mockito.when(pixmap.surface.getSize()).thenReturn(size);
        
        sendRequest(new GetGeometry(pixmap.pixmapResource.toDrawable()));
        
        GetGeometryReply reply = expectReply(GetGeometryReply.class);
        
        Mockito.verify(pixmap.surface).getDepth();
        Mockito.verify(pixmap.surface).getSize();
        
        assertThat((int)reply.getDepth().getValue()).isEqualTo(getRootDepthAsInt());
        assertThat(reply.getRoot()).isEqualTo(rootWindow);
        assertThat(reply.getWidth().getValue()).isEqualTo(size.getWidth());
        assertThat(reply.getHeight().getValue()).isEqualTo(size.getHeight());
        assertThat((int)reply.getX().getValue()).isEqualTo(0);
        assertThat((int)reply.getY().getValue()).isEqualTo(0);
        assertThat(reply.getBorderWidth().getValue()).isEqualTo(0);

        verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(pixmap.surface, pixmap.renderer);
    }
}
