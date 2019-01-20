package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.neaterbits.displayserver.buffers.PixelFormat;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.enums.VisualClass;
import com.neaterbits.displayserver.protocol.exception.ColormapException;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.AllocColorReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.AllocNamedColorReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.LookupColorReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.QueryColorsReply;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.RGB;
import com.neaterbits.displayserver.protocol.messages.requests.AllocColor;
import com.neaterbits.displayserver.protocol.messages.requests.CreateColorMap;
import com.neaterbits.displayserver.protocol.messages.requests.FreeColors;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.AllocNamedColor;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.LookupColor;
import com.neaterbits.displayserver.protocol.messages.requests.legacy.QueryColors;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.COLORMAP;
import com.neaterbits.displayserver.xwindows.model.XBuiltinColor;
import com.neaterbits.displayserver.xwindows.model.XBuiltinColors;
import com.neaterbits.displayserver.xwindows.model.XColormap;
import com.neaterbits.displayserver.xwindows.model.XColormaps;
import com.neaterbits.displayserver.xwindows.model.XScreen;
import com.neaterbits.displayserver.xwindows.model.XScreensConstAccess;
import com.neaterbits.displayserver.xwindows.model.XVisual;
import com.neaterbits.displayserver.xwindows.model.XVisualsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;
import com.neaterbits.displayserver.xwindows.util.Unsigned;

public final class XCoreColorMessageProcessor extends XOpCodeProcessor {

    private final XScreensConstAccess xScreens;
    private final XVisualsConstAccess xVisuals;
    private final XWindowsConstAccess<?> xWindows;

    private final XColormaps xColormaps;
    private final XBuiltinColors builtinColors;

    public XCoreColorMessageProcessor(
            XWindowsServerProtocolLog protocolLog,
            XScreensConstAccess xScreens,
            XVisualsConstAccess xVisuals,
            XWindowsConstAccess<?> xWindows,
            XColormaps xColormaps,
            String colorsFileName) throws IOException {

        super(protocolLog);
    
        this.xScreens = xScreens;
        this.xVisuals = xVisuals;
        this.xWindows = xWindows;

        this.xColormaps = xColormaps;
        
        final File colorsFile = new File(colorsFileName);
        
        try (FileInputStream colorsInputStream = new FileInputStream(colorsFile)) {
            this.builtinColors = XBuiltinColors.decode(colorsInputStream);
        }
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.CREATE_COLOR_MAP,
                OpCodes.ALLOC_COLOR,
                OpCodes.ALLOC_NAMED_COLOR,
                OpCodes.FREE_COLORS,
                OpCodes.QUERY_COLORS,
                OpCodes.LOOKUP_COLOR
        };
    }

    @Override
    protected void onMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException {

        switch (opcode) {

        case OpCodes.CREATE_COLOR_MAP: {
            
            final CreateColorMap createColorMap = log(messageLength, opcode, sequenceNumber, CreateColorMap.decode(stream));
            
            final XWindow xWindow = xWindows.getClientOrRootWindow(createColorMap.getWindow());
            
            if (xWindow == null) {
                sendError(client, Errors.Window, sequenceNumber, createColorMap.getWindow().getValue(), opcode);
            }
            else if (xColormaps.hasColormap(createColorMap.getMid())) {
                sendError(client, Errors.IDChoice, sequenceNumber, createColorMap.getMid().getValue(), opcode);
            }
            else {
                final Integer screenNo = xWindows.getScreenForWindow(xWindow.getWINDOW());
                
                if (screenNo == null) {
                    sendError(client, Errors.Window, sequenceNumber, createColorMap.getWindow().getValue(), opcode);
                }
                else {
                    final XScreen xScreen = xScreens.getScreen(screenNo);
                    
                    if (xScreen == null) {
                        throw new IllegalStateException();
                    }

                    final XVisual xVisual = xVisuals.getVisual(createColorMap.getVisual());
                    
                    if (xVisual == null || !xScreen.supportsVisual(xVisual, xVisuals)) {
                        sendError(client, Errors.Match, sequenceNumber, createColorMap.getVisual().getValue(), opcode);
                    }
                    else {
                        final XColormap colormap = new XColormap(xScreen, xVisual);
                        
                        xColormaps.add(createColorMap.getMid(), colormap);
                    }
                }
            }
            break;
        }
        
        case OpCodes.ALLOC_COLOR: {
            
            final AllocColor allocColor = log(messageLength, opcode, sequenceNumber, AllocColor.decode(stream));

            try {
                final PixelFormat pixelFormat = getPixelFormat(allocColor.getCmap());
    
                final int pixel = getPixel(
                        pixelFormat,
                        allocColor.getRed(),
                        allocColor.getGreen(),
                        allocColor.getBlue());
                
                sendReply(client, new AllocColorReply(
                        sequenceNumber,
                        getRed(pixelFormat, pixel),
                        getGreen(pixelFormat, pixel),
                        getBlue(pixelFormat, pixel),
                        new CARD32(Unsigned.intToUnsigned(pixel))));
            }
            catch (ColormapException ex) {
                sendError(client, Errors.Colormap, sequenceNumber, ex.getColormap().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.ALLOC_NAMED_COLOR: {
            
            final AllocNamedColor allocNamedColor = log(
                    messageLength,
                    opcode,
                    sequenceNumber,
                    AllocNamedColor.decode(stream));
            
            try {
                final PixelFormat pixelFormat = getPixelFormat(allocNamedColor.getCmap());
    
                final XBuiltinColor builtinColor = builtinColors.getColor(allocNamedColor.getName());
                
                if (builtinColor == null) {
                    sendError(client, Errors.Name, sequenceNumber, 0L, opcode);
                }
                else if (!allocNamedColor.getCmap().equals(COLORMAP.None)) {
                    throw new UnsupportedOperationException("TODO");
                }
                else {
                    final int pixel = pixelFormat.getPixel(
                            builtinColor.getR(),
                            builtinColor.getG(),
                            builtinColor.getB());

                    sendReply(client, new AllocNamedColorReply(
                        sequenceNumber,
                        
                        new CARD32(pixel),
                        new CARD16(builtinColor.getR() * 256),
                        new CARD16(builtinColor.getG() * 256),
                        new CARD16(builtinColor.getB() * 256),
                        
                        new CARD16(builtinColor.getR() * 256),
                        new CARD16(builtinColor.getG() * 256),
                        new CARD16(builtinColor.getB() * 256)));
                }
            }
            catch (ColormapException ex) {
                sendError(client, Errors.Colormap, sequenceNumber, ex.getColormap().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.FREE_COLORS: {
            
            log(messageLength, opcode, sequenceNumber, FreeColors.decode(stream));
            
            break;
        }
        
        case OpCodes.QUERY_COLORS: {
            
            final QueryColors queryColors = log(messageLength, opcode, sequenceNumber, QueryColors.decode(stream));

            try {
                final PixelFormat pixelFormat = getPixelFormat(queryColors.getCmap());
                
                final CARD32 [] pixels = queryColors.getPixels();
                
                final RGB [] colors = new RGB[pixels.length];
                
                for (int i = 0; i < pixels.length; ++ i) {
    
                    final int pixel = (int)pixels[i].getValue();
                    
                    colors[i] = new RGB(
                            getRed(pixelFormat, pixel),
                            getGreen(pixelFormat, pixel),
                            getBlue(pixelFormat, pixel));
                }
                
                sendReply(client, new QueryColorsReply(sequenceNumber, colors));
            }
            catch (ColormapException ex) {
                sendError(client, Errors.Colormap, sequenceNumber, ex.getColormap().getValue(), opcode);
            }
            break;
        }
        
        case OpCodes.LOOKUP_COLOR: {
            
            final LookupColor lookupColor = log(messageLength, opcode, sequenceNumber, LookupColor.decode(stream));

            final XBuiltinColor builtinColor = builtinColors.getColor(lookupColor.getName());
            
            if (builtinColor == null) {
                sendError(client, Errors.Name, sequenceNumber, 0L, opcode);
            }
            else if (!lookupColor.getCmap().equals(COLORMAP.None)) {
                throw new UnsupportedOperationException("TODO");
            }
            else {
                sendReply(client, new LookupColorReply(
                        sequenceNumber,
                        
                        new CARD16(builtinColor.getR() * 256),
                        new CARD16(builtinColor.getG() * 256),
                        new CARD16(builtinColor.getB() * 256),
                        
                        new CARD16(builtinColor.getR() * 256),
                        new CARD16(builtinColor.getG() * 256),
                        new CARD16(builtinColor.getB() * 256)));
            }
            break;
        }

        }
    }

    private PixelFormat getPixelFormat(COLORMAP cmap) throws ColormapException {
        
        final PixelFormat pixelFormat;
        
        if (cmap.equals(COLORMAP.None)) {
            pixelFormat = PixelFormat.RGB32;
        }
        else {
            final XColormap xColorMap = xColormaps.getColormap(cmap);
            
            if (xColorMap == null) {
                throw new ColormapException("No such colormap", cmap);
            }
            
            switch (xColorMap.getVisualClass()) {
            case VisualClass.TRUECOLOR:
                pixelFormat = PixelFormat.RGB32;
                break;
                
            default:
                throw new UnsupportedOperationException("TODO");
            }
        }

        return pixelFormat;
    }
    
    private static CARD16 getRed(PixelFormat pixelFormat, int pixel) {
        return new CARD16(pixelFormat.getRed(pixel) * 256);
    }

    private static CARD16 getGreen(PixelFormat pixelFormat, int pixel) {
        return new CARD16(pixelFormat.getGreen(pixel) * 256);
    }

    private static CARD16 getBlue(PixelFormat pixelFormat, int pixel) {
        return new CARD16(pixelFormat.getBlue(pixel) * 256);
    }
    
    private static int getPixel(PixelFormat pixelFormat, CARD16 red, CARD16 green, CARD16 blue) {
        return pixelFormat.getPixel(red.getValue() / 256, green.getValue() / 256, blue.getValue() / 256);
    }
}
