package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontCharacter;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontModel;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontProperty;
import com.neaterbits.displayserver.xwindows.fonts.model.XNamedFontModel;
import com.neaterbits.displayserver.xwindows.fonts.pcf.PCFReader;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;

public final class FontLoader {

    private final FontLoaderConfig config;
    
    public FontLoader(FontLoaderConfig config) {

        Objects.requireNonNull(config);
        
        this.config = config;
    }
    
    private XFontModel loadFontModel(String fontName) throws FileNotFoundException, IOException, NoSuchFontException {
        
        final File file = findFontFile(fontName);
        
        final XFontModel fontModel;
        
        if (file != null) {
            
            try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
            
                fontModel = PCFReader.read(inputStream);
            }
        }
        else {
            throw new NoSuchFontException("Unknown font " + fontName);
        }

        return fontModel;
    }
    
    public XFont loadFont(String fontName, FontBufferFactory fontBufferFactory) throws IOException, NoSuchFontException {
        
        final XFontModel fontModel = loadFontModel(fontName);
        
        return new XFont(fontName, fontModel, createRenderBitmaps(fontModel, fontBufferFactory));
    }
    
    
    
    private static String getFontName(File file) {
        
        final String fileName = file.getName();
    
        return getFontName(fileName);
    }

    private static String getFontName(String fileName) {

        final String name;
        
        if (fileName.endsWith(".pcf")) {
            name = fileName.substring(0, fileName.length() - ".pcf".length());
        }
        else if (fileName.endsWith(".pcf.gz")) {
            name = fileName.substring(0, fileName.length() - ".pcf.gz".length());
        }
        else {
            name = fileName;
        }
        
        return name;
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
                fontModel = loadFontModel(fontName);
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
        
            iterateFontFiles(
                file -> {
                    final List<XFontProperty> properties = readProperties(file);
                    
                    if (properties != null) {
                        final XLFD fontXlfd = XLFD.fromFontProperties(properties);
                        
                        if (xlfd.matchesFont(fontXlfd)) {
                            
                            final String fontName = getFontName(file);
                            
                            fontNames.add(new FontDescriptor(fontName, fontXlfd));
                        }
                    }

                    return null;
                },
                null);
        }

        return fontNames;
    }
    
    static List<XFontProperty> readProperties(File file) throws IOException {
        
        final List<XFontProperty> properties;
        
        final FontFileType type = FontFileType.getFontFileType(file);
        
        if (type == null) {
            properties = null;
        }
        else {
        
            switch (type) {
            case PCF:
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    properties = PCFReader.readProperties(inputStream);
                }
                break;
                
            case PCF_COMPRESSED:
                try (GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(file))) {
                    properties = PCFReader.readProperties(inputStream);
                }
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown font file type " + type);
            }
        }
        
        return properties;
    }
    
    @FunctionalInterface
    interface FontFileIterator<T> {
        T onFontFile(File file) throws IOException;
    }
    
    private <T> T iterateFontFiles(FontFileIterator<T> processFile, FilenameFilter filenameFilter) {
        
        for (File path : config.getFontPaths()) {

            if (path.exists() && path.isDirectory()) {
                
                final File[] files = filenameFilter != null
                        ? path.listFiles(filenameFilter)
                        : path.listFiles();

                for (File file : files) {
                    
                    if (file.exists() && file.canRead() && isFontFile(file)) {
                        
                        try {
                            final T result = processFile.onFontFile(file);
                            
                            if (result != null) {
                                return result;
                            }
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        return null;
    }
    
    private List<FontDescriptor> getFontNamesByName(String pattern) throws ValueException {

        final Pattern regexPattern = FontMatchUtil.getFontMatchGlobPattern(pattern);
        
        final FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                final String lowercaseName = getFontName(name).toLowerCase();

                final Matcher matcher = regexPattern.matcher(lowercaseName);

                return matcher.matches();
            }
        };

        final List<FontDescriptor> fontNames = new ArrayList<>();
        
        iterateFontFiles(
                file -> fontNames.add(new FontDescriptor(getFontName(file))),
                filenameFilter);

        
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

    private static boolean isFontFile(File file) {
        return getFontFileType(file) != null;
    }

        
    private static FontFileType getFontFileType(File file) {
        
        final String fontFilename = file.getName();
        
        final FontFileType fileType;
        
        if (fontFilename.endsWith(".pcf")) {
            fileType = FontFileType.PCF;
        }
        else if (fontFilename.endsWith(".pcf.gz")) {
            fileType = FontFileType.PCF_COMPRESSED;
        }
        else {
            fileType = null;
        }
    
        return fileType;
    }
    
    private File findFontFile(String fontName) {
        
        return iterateFontFiles(
                file -> {
                    for (FontFileType type : FontFileType.values()) {
                        if (file.getName().equals(fontName + type.getExtension())) {
                            return file;
                        }
                    }
                    
                    return null;
                },
                null);
    }
}
