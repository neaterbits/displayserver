package com.neaterbits.displayserver.xwindows.core.processing;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.neaterbits.displayserver.buffers.GetImageListener;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.ImageFormat;
import com.neaterbits.displayserver.protocol.enums.gc.Function;
import com.neaterbits.displayserver.protocol.exception.GContextException;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.messages.requests.CreateGC;
import com.neaterbits.displayserver.protocol.messages.requests.GetImage;
import com.neaterbits.displayserver.protocol.messages.requests.PutImage;
import com.neaterbits.displayserver.protocol.messages.requests.XGCAttributes;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.DRAWABLE;
import com.neaterbits.displayserver.protocol.types.GCONTEXT;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.types.Position;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.xwindows.core.util.XGCAttributesBuilder;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

import static org.mockito.Mockito.when;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.ArgumentMatchers.isNotNull;

import static org.assertj.core.api.Assertions.assertThat;

public class XCoreImageTest extends BaseXCorePixmapTest {

    private void checkPutAndGetImage(DRAWABLE drawable, Surface surface, XLibRenderer renderer, Size drawableSize) {
        
        final GCONTEXT gc = new GCONTEXT(allocateResourceId());

        final XGCAttributes attributes = new XGCAttributesBuilder()
                .addFunction(Function.Copy)
                .build();
        
        final CreateGC createGC = new CreateGC(gc, drawable, attributes);
        
        sendRequest(createGC);

        try {
            verify(client).createGC(isNotNull());
        } catch (IDChoiceException ex) {
            throw new IllegalStateException(ex);
        }

        final XGC xgc = new XGC(attributes);
        
        final byte [] imageData = new byte [] {
                0x00, 0x01, 0x02,
                0x0A, 0x0B, 0x0C,
                0x03, 0x04, 0x05,
                
                0x10, 0x11, 0x12,
                0x1A, 0x1B, 0x1C,
                0x13, 0x14, 0x15,

                0x20, 0x21, 0x22,
                0x2A, 0x2B, 0x2C,
                0x23, 0x24, 0x25,
        };
        
        final int depth = 24;
        
        final Position position = new Position(25, 35);
        final Size size = new Size(3, 3);
        
        final BYTE imageFormat = ImageFormat.ZPixMap;
        
        final PutImage putImage = new PutImage(
                imageFormat,
                drawable,
                gc,
                new CARD16(size.getWidth()),
                new CARD16(size.getHeight()),
                new INT16((short)position.getLeft()),
                new INT16((short)position.getTop()),
                new CARD8((short)0),
                new CARD8((short)depth),
                imageData,
                0,
                imageData.length);
        
        
        try {
            when(client.getGC(eq(gc))).thenReturn(xgc);
        } catch (GContextException ex) {
            throw new IllegalStateException(ex);
        }
        
        sendRequest(putImage);

        final int padding = XWindowsProtocolUtil.getPadding(imageData.length);
        
        final byte [] paddedData = Arrays.copyOf(imageData, imageData.length + padding);
        
        try {
            verify(client).getGC(eq(gc));
        } catch (GContextException ex) {
            throw new IllegalStateException(ex);
        }
        
        verify(renderer).putImage(
                same(xgc),
                eq((int)imageFormat.getValue()),
                eq(size.getWidth()),
                eq(size.getHeight()),
                eq(position.getLeft()),
                eq(position.getTop()),
                eq(0),
                eq(depth),
                eq(paddedData));
        
        final GetImage getImage = new GetImage(
                imageFormat,
                drawable,
                new INT16((short)position.getLeft()),
                new INT16((short)position.getTop()),
                new CARD16(size.getWidth()),
                new CARD16(size.getHeight()),
                new CARD32(0xFFFFFFFFL));

        when(surface.getWidth()).thenReturn(drawableSize.getWidth());
        when(surface.getHeight()).thenReturn(drawableSize.getHeight());

        final ArgumentCaptor<GetImageListener> getImageListener = ArgumentCaptor.forClass(GetImageListener.class);
        
        sendRequest(getImage);
        
        verify(surface).getWidth();
        verify(surface).getHeight();

        verify(surface).getImage(
                eq(position.getLeft()),
                eq(position.getTop()),
                eq(size.getWidth()),
                eq(size.getHeight()),
                same(rootPixelFormat),
                getImageListener.capture());

        verify(renderer).flush();
        
        final ArgumentCaptor<GetImageReply> getImageReply = ArgumentCaptor.forClass(GetImageReply.class);
        
        when(surface.getDepth()).thenReturn(rootPixelFormat.getDepth());
        
        getImageListener.getValue().onResult(imageData);

        verify(surface).getDepth();
        verify(client).send(getImageReply.capture());
        
        final byte [] returnedData = getImageReply.getValue().getData();
        
        assertThat(returnedData).isEqualTo(imageData);
    }
    
    @Test
    public void testPutAndGetImageToPixmap() {
        
        final int depth = getRootDepthAsInt();
        
        final Size drawableSize = new Size(250, 150);
        
        final PixmapState pixmapState = checkCreatePixmap(depth, rootWindow.toDrawable(), drawableSize);
        
        checkPutAndGetImage(
                pixmapState.pixmapResource.toDrawable(),
                pixmapState.surface,
                pixmapState.renderer,
                drawableSize);
        
        checkFreePixmap(pixmapState);
    }
}
