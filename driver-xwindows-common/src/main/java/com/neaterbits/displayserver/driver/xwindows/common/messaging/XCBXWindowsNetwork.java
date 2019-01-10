package com.neaterbits.displayserver.driver.xwindows.common.messaging;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.SentRequest;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsNetwork;
import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.DEPTH;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.FORMAT;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.SCREEN;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.VISUALTYPE;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.protocol.types.KEYCODE;
import com.neaterbits.displayserver.protocol.types.SET32;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.render.cairo.xcb.XCBConnection;
import com.neaterbits.displayserver.render.cairo.xcb.XCBDepth;
import com.neaterbits.displayserver.render.cairo.xcb.XCBFormat;
import com.neaterbits.displayserver.render.cairo.xcb.XCBScreen;
import com.neaterbits.displayserver.render.cairo.xcb.XCBSetup;
import com.neaterbits.displayserver.render.cairo.xcb.XCBVisual;

public class XCBXWindowsNetwork implements XWindowsNetwork {

    private final XCBConnection xcbConnection;

    public XCBXWindowsNetwork(XCBConnection xcbConnection) {

        Objects.requireNonNull(xcbConnection);
        
        this.xcbConnection = xcbConnection;
    }
    
    
    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public ServerMessage getInitialMessage() {
        return makeServerMessage(xcbConnection.getSetup());
    }

    @Override
    public SentRequest sendRequest(Request request, ByteOrder byteOrder) {
        
        final DataWriter dataWriter = Encodeable.makeDataWriter(request);
        final byte [] data = DataWriter.writeToBuf(dataWriter, byteOrder);
        
        System.out.println("## writing request " + request.getOpCode() + " of length " + data.length);

        final boolean hasReply = request.getReplyClass() != null;
        
        final int sequenceNumber = xcbConnection.sendRequest(data, request.getOpCode(), !hasReply);
        
        final ByteBuffer byteBuffer;
        
        if (hasReply) {
            final byte [] reply = xcbConnection.waitForReply(sequenceNumber);
            byteBuffer = ByteBuffer.wrap(reply);
            
            byteBuffer.order(getByteOrder());
            
            System.out.println("## reply length: " + reply.length);
        }
        else {
            byteBuffer = null;
        }
        
        return new SentRequest(sequenceNumber, data.length, byteBuffer);
    }

    @Override
    public int send(Encodeable message, ByteOrder byteOrder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
        xcbConnection.close();
    }
    
    private static ServerMessage makeServerMessage(XCBSetup setup) {
        
        return new ServerMessage(
                new BYTE((byte)setup.getSuccess()),
                new CARD16(setup.getProtocolMajorVersion()),
                new CARD16(setup.getProtocolMinorVersion()),
                new CARD32(setup.getReleaseNumber()),
                new CARD32(setup.getResourceIdBase()),
                new CARD32(setup.getResourceIdMask()),
                new CARD32(setup.getMotionBufferSize()),
                new CARD16(setup.getMaximumRequestLength()),
                new BYTE((byte)setup.getImageByteOrder()),
                new BYTE((byte)setup.getBitmapFormatBitOrder()),
                new CARD8((short)setup.getBitmapFormatScanlineUnit()),
                new CARD8((short)setup.getBitmapFormatScanlinePad()),
                new KEYCODE((short)setup.getMinKeyCode()),
                new KEYCODE((short)setup.getMaxKeyCode()),
                setup.getVendor(),
                makePixmapFormats(setup.getPixmapFormats()),
                makeScreens(setup.getScreens()));
        
    }
    
    private static FORMAT [] makePixmapFormats(List<XCBFormat> formats) {
        
        final FORMAT [] result = new FORMAT[formats.size()];
        
        int dstIdx = 0;
        
        for (XCBFormat format : formats) {
            
            result[dstIdx ++] = new FORMAT(
                    new CARD8((short)format.getDepth()),
                    new CARD8((short)format.getBitsPerPixel()),
                    new CARD8((short)format.getScanlinePad()));
        }
        
        return result;
    }

    private static SCREEN [] makeScreens(List<XCBScreen> screens) {
        
        final SCREEN [] result = new SCREEN[screens.size()];
        
        int dstIdx = 0;
        
        for (XCBScreen screen : screens) {
            
            result[dstIdx ++] = new SCREEN(
                    new WINDOW(screen.getRoot()),
                    new COLORMAP(screen.getDefaultColorMap()),
                    new CARD32(screen.getWhitePixel()),
                    new CARD32(screen.getBlackPixel()),
                    new SET32((int)screen.getCurrentInputMasks()),
                    new CARD16(screen.getWidthInPixels()),
                    new CARD16(screen.getHeightInPixels()),
                    new CARD16(screen.getWidthInMillimiters()),
                    new CARD16(screen.getHeightInMillimiters()),
                    new CARD16(screen.getMinInstalledMaps()),
                    new CARD16(screen.getMaxInstalledMaps()),
                    new VISUALID(screen.getRootVisual()),
                    new BYTE((byte)screen.getBackingStores()),
                    new BOOL((byte)screen.getSaveUnders()),
                    new CARD8((short)screen.getRootDepth()),
                    makeDepths(screen.getDepths()));
        }
        
        return result;
    }
    
    private static DEPTH [] makeDepths(List<XCBDepth> depths) {
        
        final DEPTH [] result = new DEPTH[depths.size()];
        
        int dstIdx = 0;
        
        for (XCBDepth depth : depths) {
            
            result[dstIdx ++] = new DEPTH(
                    new CARD8(depth.getDepth()),
                    makeVisuals(depth.getVisuals()));
        }
        
        return result;
    }
    
    private static VISUALTYPE [] makeVisuals(List<XCBVisual> visuals) {
        
        final VISUALTYPE [] result = new VISUALTYPE[visuals.size()];
        
        int dstIx = 0;
        
        for (XCBVisual visual : visuals) {
            
            result[dstIx ++] = new VISUALTYPE(
                    new VISUALID(visual.getVisualId()),
                    new BYTE(visual.getVisualClass()),
                    new CARD8(visual.getBitsPerRGBValue()),
                    new CARD16(visual.getColormapEntries()),
                    new CARD32(visual.getRedMask()),
                    new CARD32(visual.getGreenMask()),
                    new CARD32(visual.getBlueMask()));
        }
        
        return result;
    }
}
