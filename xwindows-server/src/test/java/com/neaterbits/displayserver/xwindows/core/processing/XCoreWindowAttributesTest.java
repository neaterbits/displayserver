package com.neaterbits.displayserver.xwindows.core.processing;

import org.junit.Test;
import org.mockito.Mockito;

import com.neaterbits.displayserver.protocol.enums.Alloc;
import com.neaterbits.displayserver.protocol.enums.BackingStore;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.MapState;
import com.neaterbits.displayserver.protocol.enums.WindowClass;
import com.neaterbits.displayserver.protocol.exception.IDChoiceException;
import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.replies.GetWindowAttributesReply;
import com.neaterbits.displayserver.protocol.messages.requests.ChangeWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.CreateColorMap;
import com.neaterbits.displayserver.protocol.messages.requests.CreateCursor;
import com.neaterbits.displayserver.protocol.messages.requests.GetWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.XWindowAttributes;
import com.neaterbits.displayserver.protocol.types.BITGRAVITY;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.CURSOR;
import com.neaterbits.displayserver.protocol.types.PIXMAP;
import com.neaterbits.displayserver.protocol.types.SETofDEVICEEVENT;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINGRAVITY;
import com.neaterbits.displayserver.types.Size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class XCoreWindowAttributesTest extends BaseXCorePixmapTest {

    @Test
    public void testSetInvalidBackgroundPixmap() {

        final WindowState window = checkCreateWindow();

        final PIXMAP backgroundPixmap = new PIXMAP(allocateResourceId());

        final XError error = changeWindowAttributesAndExpectError(window, builder -> builder.setBackgroundPixmap(backgroundPixmap));
        
        assertThat(error.getCode()).isEqualTo(Errors.Pixmap);
        assertThat(error.getValue().getValue()).isEqualTo(backgroundPixmap.getValue());
    }

    @Test
    public void testSetInvalidBorderPixmap() {

        final WindowState window = checkCreateWindow();

        final PIXMAP borderPixmap = new PIXMAP(allocateResourceId());

        final XError error = changeWindowAttributesAndExpectError(window, builder -> builder.setBorderPixmap(borderPixmap));
        
        assertThat(error.getCode()).isEqualTo(Errors.Pixmap);
        assertThat(error.getValue().getValue()).isEqualTo(borderPixmap.getValue());
    }

    @Test
    public void testSetInvalidColorMap() {

        final WindowState window = checkCreateWindow();

        final COLORMAP colormap = new COLORMAP(allocateResourceId());

        final XError error = changeWindowAttributesAndExpectError(window, builder -> builder.setColormap(colormap));
        
        assertThat(error.getCode()).isEqualTo(Errors.Colormap);
        assertThat(error.getValue().getValue()).isEqualTo(colormap.getValue());
    }
    
    @Test
    public void testSetInvalidCursor() {

        final WindowState window = checkCreateWindow();

        final CURSOR cursor = new CURSOR(allocateResourceId());

        final XError error = changeWindowAttributesAndExpectError(window, builder -> builder.setCursor(cursor));
        
        assertThat(error.getCode()).isEqualTo(Errors.Cursor);
        assertThat(error.getValue().getValue()).isEqualTo(cursor.getValue());
    }

    @Test
    public void testGetAndSetAttributes() {

        final WindowState window = checkCreateWindow();

        final PIXMAP backgroundPixmap = checkCreatePixmap(getRootDepthAsInt(), window.windowResource.toDrawable(), new Size(150, 100))
                        .pixmapResource;
        
        Mockito.reset(displayArea);
        
        final PIXMAP borderPixmap = checkCreatePixmap(getRootDepthAsInt(), window.windowResource.toDrawable(), new Size(150, 100))
                        .pixmapResource;

        Mockito.reset(displayArea);
        
        final COLORMAP colormap = new COLORMAP(allocateResourceId());
        sendRequest(new CreateColorMap(Alloc.None, colormap, window.windowResource, getRootVisual()));
        
        final PIXMAP cursorPixmap = checkCreatePixmap(getRootDepthAsInt(), window.windowResource.toDrawable(), new Size(150, 100))
                .pixmapResource;

        Mockito.reset(displayArea);

        final PIXMAP cursorMaskPixmap = checkCreatePixmap(1, window.windowResource.toDrawable(), new Size(150, 100))
                .pixmapResource;

        final CURSOR cursor = new CURSOR(allocateResourceId());

        final CreateCursor createCursor = new CreateCursor(
                cursor,
                cursorPixmap, cursorMaskPixmap,
                new CARD16(0), new CARD16(0), new CARD16(0),
                new CARD16(0), new CARD16(0), new CARD16(0),
                new CARD16(0), new CARD16(0));

        sendRequest(createCursor);
   
        try {
            verify(client).checkAndAddResourceId(eq(cursor));
        } catch (IDChoiceException ex) {
            throw new IllegalStateException(ex);
        }

        final XWindowAttributes attributes = new XWindowAttributes(
                XWindowAttributes.ALL,
                backgroundPixmap,
                new CARD32(0x00506070L),
                borderPixmap,
                new CARD32(0x00102030L),
                BITGRAVITY.Center,
                WINGRAVITY.SouthWest,
                BackingStore.WhenMapped,
                new CARD32(0x00A0B0C0L),
                new CARD32(0x00304050L),
                BOOL.True,
                BOOL.True,
                new SETofEVENT(SETofEVENT.KEY_PRESS|SETofEVENT.KEYMAP_STATE),
                new SETofDEVICEEVENT(SETofEVENT.BUTTON_PRESS|SETofEVENT.BUTTON_RELEASE),
                colormap,
                cursor);
        
        sendRequest(new ChangeWindowAttributes(window.windowResource, attributes));
    
        final GetWindowAttributes getWindowAttributes = new GetWindowAttributes(window.windowResource);
        
        sendRequest(getWindowAttributes);
        
        final GetWindowAttributesReply reply = expectReply(GetWindowAttributesReply.class);
        
        assertThat(reply.getBackingStore()).isEqualTo(attributes.getBackingStore());
        assertThat(reply.getVisual()).isEqualTo(getRootVisual());
        assertThat(reply.getWindowClass()).isEqualTo(WindowClass.InputOutput);
        assertThat(reply.getBitGravity()).isEqualTo(attributes.getBitGravity());
        assertThat(reply.getWinGravity()).isEqualTo(attributes.getWinGravity());
        assertThat(reply.getBackingPlanes()).isEqualTo(attributes.getBackingPlanes());
        assertThat(reply.getBackingPixel()).isEqualTo(attributes.getBackingPixel());
        assertThat(reply.getSaveUnder()).isEqualTo(attributes.getSaveUnder());
        assertThat(reply.getOverrideRedirect()).isEqualTo(attributes.getOverrideRedirect());
        assertThat(reply.getColormap()).isEqualTo(attributes.getColormap());
        assertThat(reply.getAllEventMasks()).isEqualTo(attributes.getEventMask());
        assertThat(reply.getYourEventMasks()).isEqualTo(attributes.getEventMask());
        assertThat(reply.getDoNotPropagateMask()).isEqualTo(attributes.getDoNotPropagateMask());
        assertThat(reply.getMapIsInstalled()).isEqualTo(BOOL.True);
        assertThat(reply.getMapState()).isEqualTo(MapState.Viewable);

        verifyNoMoreInteractions(window);
    }
}
