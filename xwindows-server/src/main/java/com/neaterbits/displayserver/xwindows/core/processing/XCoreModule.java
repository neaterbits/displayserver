package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.neaterbits.displayserver.buffers.ImageBufferFormat;
import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.server.XClientWindows;
import com.neaterbits.displayserver.server.XConfig;
import com.neaterbits.displayserver.server.XEventSubscriptions;
import com.neaterbits.displayserver.server.XTimestampGenerator;
import com.neaterbits.displayserver.windows.WindowManagement;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;
import com.neaterbits.displayserver.xwindows.model.XColormaps;
import com.neaterbits.displayserver.xwindows.model.XCursors;
import com.neaterbits.displayserver.xwindows.model.XPixmaps;
import com.neaterbits.displayserver.xwindows.model.XScreensConstAccess;
import com.neaterbits.displayserver.xwindows.model.XVisualsConstAccess;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;
import com.neaterbits.displayserver.xwindows.processing.XModule;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public class XCoreModule extends XModule {

    public XCoreModule(
            XWindowsServerProtocolLog protocolLog,
            WindowManagement windowManagement,
            XScreensConstAccess screens,
            XVisualsConstAccess visuals,
            XClientWindows windows,
            XPixmaps pixmaps,
            XColormaps colormaps,
            XCursors cursors,
            XEventSubscriptions eventSubscriptions,
            Set<ImageBufferFormat> imageBufferFormats,
            Compositor compositor,
            XLibRendererFactory rendererFactory,
            FontBufferFactory fontBufferFactory,
            XTimestampGenerator timestampGenerator,
            InputDriver inputDriver,
            XConfig config) throws IOException {
        
        super(makeProcessors(
                protocolLog,
                windowManagement,
                screens,
                visuals,
                windows,
                pixmaps,
                colormaps,
                cursors,
                eventSubscriptions,
                imageBufferFormats,
                compositor,
                rendererFactory,
                fontBufferFactory,
                timestampGenerator,
                inputDriver,
                config));
    }

    private static Collection<XOpCodeProcessor> makeProcessors(
            XWindowsServerProtocolLog protocolLog,
            WindowManagement windowManagement,
            XScreensConstAccess screens,
            XVisualsConstAccess visuals,
            XClientWindows windows,
            XPixmaps pixmaps,
            XColormaps colormaps,
            XCursors cursors,
            XEventSubscriptions eventSubscriptions,
            Set<ImageBufferFormat> imageBufferFormats,
            Compositor compositor,
            XLibRendererFactory rendererFactory,
            FontBufferFactory fontBufferFactory,
            XTimestampGenerator timestampGenerator,
            InputDriver inputDriver,
            XConfig config) throws IOException {
        
        final List<XOpCodeProcessor> processors = Arrays.asList(

                new XCoreWindowMessageProcessor(
                        protocolLog,
                        windowManagement,
                        windows,
                        pixmaps,
                        colormaps,
                        cursors,
                        eventSubscriptions,
                        compositor,
                        rendererFactory),
                
                new XCorePixmapMessageProcessor(
                        protocolLog,
                        windows,
                        pixmaps,
                        rendererFactory),
                
                new XCoreAtomMessageProcessor(protocolLog),
                
                new XCorePropertyMessageProcessor(protocolLog, windows, timestampGenerator),
                
                new XCoreSelectionMessageProcessor(protocolLog),
                
                new XCoreSendEventMessageProcessor(protocolLog),
                
                new XCoreGrabMessageProcessor(protocolLog),
                
                new XCorePointerMessageProcessor(protocolLog, windows),
                
                new XCoreFocusMessageProcessor(protocolLog),
                
                new XCoreFontMessageProcessor(protocolLog, config.getFontConfig(), fontBufferFactory),
                
                new XCoreGCMessageProcessor(protocolLog, windows, pixmaps),
                
                new XCoreAreaMessageProcessor(protocolLog, windows, pixmaps),
                
                new XCoreDrawMessageProcessor(protocolLog, windows, pixmaps),
                
                new XCoreImageMessageProcessor(protocolLog, windows, pixmaps, imageBufferFormats),
                
                new XCoreTextMessageProcessor(protocolLog, windows, pixmaps),
                
                new XCoreColorMessageProcessor(
                        protocolLog,
                        screens,
                        visuals,
                        windows,
                        colormaps,
                        config.getColorsFile()),
                
                new XCoreCursorMessageProcessor(protocolLog, cursors),
                
                new XCoreExtensionMessageProcessor(protocolLog),
                
                new XCoreKeyboardMessageProcessor(protocolLog, inputDriver),
                
                new XCoreMiscMessageProcessor(protocolLog)
        );

        return processors;
    }
}
