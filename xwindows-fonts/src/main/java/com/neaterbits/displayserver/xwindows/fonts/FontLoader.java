package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontModel;
import com.neaterbits.displayserver.xwindows.fonts.model.XNamedFontModel;
import com.neaterbits.displayserver.xwindows.fonts.pcf.PCFReader;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;

public final class FontLoader {

    private final FontLoaderConfig config;
    
    private final FontSource fontSource;
    
    public FontLoader(FontLoaderConfig config) {

        Objects.requireNonNull(config);
        
        this.config = config;
        this.fontSource = new CacheFontSource(
                new FileSystemFontSource(config.getFontPaths())
        );
    }
    
    private XFontModel loadFontModel(FontDescriptor fontDescriptor) throws FileNotFoundException, IOException, NoSuchFontException {
        
        final File file = fontSource.findFontFile(fontDescriptor);
        
        final XFontModel fontModel;
        
        if (file != null) {
            
            try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
            
                fontModel = PCFReader.read(inputStream);
            }
        }
        else {
            throw new NoSuchFontException("Unknown font " + fontDescriptor);
        }

        return fontModel;
    }
    
    public XFont loadFont(FontDescriptor fontDescriptor, FontBufferFactory fontBufferFactory) throws IOException, NoSuchFontException {
        
        final XFontModel fontModel = loadFontModel(fontDescriptor);
        
        return new XFont(fontDescriptor.getFontName(), fontModel, createRenderBitmaps(fontModel, fontBufferFactory));
    }
    
    
    public FontDescriptor [] listFonts(String pattern) throws ValueException {
        
        XLFD xlfd = null;
        
        try {
            xlfd = XLFD.decode(pattern, true);
        } catch (XFLDException ex) {
            
        }
        
        final List<FontDescriptor> fontNames = listFonts(pattern, xlfd);
        
        return fontNames.toArray(new FontDescriptor[fontNames.size()]);
    }

    private List<FontDescriptor> listFonts(String pattern, XLFD xlfd) throws ValueException {

        final List<FontDescriptor> fontNames;
        
        if (xlfd != null) {
            fontNames = getFontNamesByXLFD(xlfd);
        }
        else {
            fontNames = getFontNamesByName(pattern);
        }
    
        return fontNames;
    }

    public XNamedFontModel [] listFontsWithInfo(String pattern) throws ValueException {

        final FontDescriptor [] fontDescriptors = listFonts(pattern);
        
        final List<XNamedFontModel> fonts = new ArrayList<>(fontDescriptors.length);
        
        for (FontDescriptor fontDescriptor : fontDescriptors) {
            
            final String fontName = fontDescriptor.getFontName();
            
            XFontModel fontModel = null;
            
            try {
                fontModel = loadFontModel(fontDescriptor);
            } catch (IOException | NoSuchFontException ex) {
            }
            
            if (fontModel != null) {
                fonts.add(new XNamedFontModel(fontName, fontModel));
            }
        }
        
        return fonts.toArray(new XNamedFontModel[fonts.size()]);
    }

    private static boolean lookupFromAliasFile(XLFD xlfd, String aliasFile, List<FontDescriptor> fontNames) {
        
        final boolean fontsLookedUp;
        
        if (aliasFile != null) {
            
            final File file = new File(aliasFile);
            
            if (file.exists() && file.canRead()) {
            
                FontAliases fontAliases = null;
                
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    fontAliases = FontAliasFileReader.read(inputStream);
                }
                catch (Exception ex) {
                    
                }
                
                if (fontAliases != null) {
                    fontAliases.getFontNamesForXLFD(xlfd, fontNames);
                    
                    fontsLookedUp = true;
                }
                else {
                    fontsLookedUp = false;
                }
            }
            else {
                fontsLookedUp = false;
            }
        }
        else {
            fontsLookedUp = false;
        }
        
        return fontsLookedUp;
    }
    
    private List<FontDescriptor> getFontNamesByXLFD(XLFD xlfd) throws ValueException {
    
        Objects.requireNonNull(xlfd);
        
        final List<FontDescriptor> fontNames = new ArrayList<>();
        
        if (!lookupFromAliasFile(xlfd, config.getBaseFontsAliasFile(), fontNames)) {

            fontSource.iterateFontsByXLFD(xlfd, fontNames::add);
            
        }

        return fontNames;
    }
    
    
    private List<FontDescriptor> getFontNamesByName(String pattern) throws ValueException {


        final List<FontDescriptor> fontNames = new ArrayList<>();
        
        final Pattern regexPattern = FontMatchUtil.getFontMatchGlobPattern(pattern);

        fontSource.iterateFontsByName(regexPattern, fontNames::add);
        
        return fontNames;
    }
    
    private static FontBuffer [] createRenderBitmaps(XFontModel fontModel, FontBufferFactory fontBufferFactory) {
        
        final List<byte []> bitmaps = fontModel.getBitmaps().getBitmaps();
        
        final FontBuffer [] fontBuffers = new FontBuffer[bitmaps.size()];
        
        for (int i = 0; i < bitmaps.size(); ++ i) {
            
            final XFontCharacter metrics = fontModel.getMetrics().get(i);
            
            fontBuffers[i] = fontBufferFactory.createFontBuffer(
                    i,
                    bitmaps.get(i),
                    fontModel.getBitmaps().getBitmapFormat(),
                    metrics.getCharacterWidth(),
                    metrics.getAscent() + metrics.getDescent());
            
        }
        
        
        return fontBuffers;
    }

    
}
