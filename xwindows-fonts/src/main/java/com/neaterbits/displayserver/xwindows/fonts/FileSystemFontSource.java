package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.neaterbits.displayserver.xwindows.fonts.model.XFontProperty;

final class FileSystemFontSource extends FontSource {

    private final List<File> fontPaths;

    public FileSystemFontSource(List<File> fontPaths) {

        Objects.requireNonNull(fontPaths);
        
        if (fontPaths.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        this.fontPaths = fontPaths;
    }
    
    @FunctionalInterface
    interface FontFileIterator<T> {
        T onFontFile(File file) throws IOException;
    }
    
    @Override
    <T> void iterateFonts(FontDescriptorIterator iterator) {

        iterateFontFiles(
                file -> {
                    iterator.onFontDescriptor(new FontDescriptor(getFontName(file)));
                    
                    return null;
                },
                null);
    }

    @Override
    <T> void iterateFontsByName(Pattern pattern, FontDescriptorIterator iterator) {
        
        final FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                
                return matchesFontName(pattern, getFontName(name));
            }
        };

        iterateFontFiles(
                file -> {
                    iterator.onFontDescriptor(new FontDescriptor(getFontName(file)));
                    
                    return null;
                },
                filenameFilter);
    }
    
    @Override
    <T> void iterateFontsByXLFD(XLFD xlfd, FontDescriptorIterator iterator) {
        iterateFontFiles(
                file -> {
                    final List<XFontProperty> properties = FontUtil.readProperties(file);
                    
                    if (properties != null) {
                        final XLFD fontXlfd = XLFD.fromFontProperties(properties);
                        
                        if (xlfd.matchesFont(fontXlfd)) {
                            
                            final String fontName = getFontName(file);
                            
                            iterator.onFontDescriptor(new FontDescriptor(fontName, fontXlfd));
                        }
                    }

                    return null;
                },
                null);
    }

    @Override
    File findFontFile(FontDescriptor fontDescriptor) {

        return findFontFile(fontDescriptor.getFontName());
    }

    private <T> T iterateFontFiles(FontFileIterator<T> processFile, FilenameFilter filenameFilter) {
        
        for (File path : fontPaths) {

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
