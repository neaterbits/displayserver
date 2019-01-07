package com.neaterbits.displayserver.xwindows.fonts;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontIntegerProperty;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontProperty;
import com.neaterbits.displayserver.xwindows.fonts.model.XFontStringProperty;

public final class XLFD {

    private final StringValue foundry;
    private final StringValue familyName;
    private final StringValue weightName;
    private final StringValue slant;
    private final StringValue setwidthName;
    private final StringValue addStyleName;
    
    private final IntegerValue pixelSize;
    private final IntegerValue pointSize;
    
    private final IntegerValue resolutionX;
    private final IntegerValue resolutionY;
    
    private final StringValue spacing;
    
    private final IntegerValue averageWidth;

    private final StringValue charsetRegistry;
    private final StringValue charsetEncoding;
    
    private final Pattern regexPattern;
    
    public static XLFD decode(String string, boolean regexPattern) throws XFLDException {
        
        Objects.requireNonNull(string);
        
        final String [] parts = string.split("-");
        
        if (parts.length < 15) {
            throw new XFLDException("parts.lengh < 15");
        }
        
        if (!parts[0].isEmpty()) {
            throw new XFLDException("Expected empty first part of " + string);
        }
        
        return new XLFD(
                parseString(parts[1]),
                parseString(parts[2]),
                parseString(parts[3]),
                parseString(parts[4]),
                parseString(parts[5]),
                parseString(parts[6]),
                parseInteger(parts[7]),
                parseInteger(parts[8]),
                parseInteger(parts[9]),
                parseInteger(parts[10]),
                parseString(parts[11]),
                parseInteger(parts[12]),
                parseString(parts[13]),
                parseString(parts[14]),
                regexPattern ? string : null);
        
    }
    
    static XLFD fromFontProperties(List<XFontProperty> properties) {
        
        String foundry = null;
        String familyName = null;
        String weightName = null;
        String slant = null;
        String setwidthName = null;
        
        
        String addStyleName = null;
        
        Integer pixelSize = null;
        Integer pointSize = null;
        
        Integer resolutionX = null;
        Integer resolutionY = null;
        
        String spacing = null;
        
        Integer averageWidth = null;
        String charsetRegistry = null;
        String charsetEncoding = null;

        for (XFontProperty property : properties) {
            
            switch (property.getName()) {
            case "FOUNDRY":
                foundry = getPropertyString(property);
                break;

            case "FAMILY_NAME":
                familyName = getPropertyString(property);
                break;

            case "WEIGHT_NAME":
               weightName = getPropertyString(property);
               break;

            case "SLANT":
               slant = getPropertyString(property);
               break;

            case "ADD_STYLE_NAME":
               addStyleName = getPropertyString(property);
               break;

            case "PIXEL_SIZE":
               pixelSize = getPropertyInteger(property);
               break;

            case "POINT_SIZE":
               pointSize = getPropertyInteger(property);
               break;

            case "RESOLUTION_X":
               resolutionX = getPropertyInteger(property);
               break;

            case "RESOULTION_Y":
               resolutionY = getPropertyInteger(property);
               break;

            case "SPACING":
               spacing = getPropertyString(property);
               break;

            case "AVERAGE_WIDTH":
                averageWidth = getPropertyInteger(property);
                break;

            case "CHARSET_REGISTRY":
               charsetRegistry = getPropertyString(property);
               break;

            case "CHARSET_ENCODING":
               charsetEncoding = getPropertyString(property);
               break;

            }
        }
        
        return new XLFD(
                foundry,
                familyName,
                weightName,
                slant,
                setwidthName,
                addStyleName,
                pixelSize,
                pointSize,
                resolutionX,
                resolutionY,
                spacing,
                averageWidth,
                charsetRegistry,
                charsetEncoding,
                null);
    }
    
    private static String getPropertyString(XFontProperty property) {
        return property instanceof XFontStringProperty
                ? ((XFontStringProperty)property).getValue()
                : null;
    }

    private static Integer getPropertyInteger(XFontProperty property) {
        return property instanceof XFontIntegerProperty
                ? ((XFontIntegerProperty)property).getValue()
                : null;
    }
    
    boolean matchesFont(XLFD fontXlfd) {

        final String xlfdLowercase = fontXlfd.asString().toLowerCase();
        
        final Matcher matcher = regexPattern.matcher(xlfdLowercase);
        
        return matcher.matches();
    }
    
    private static StringValue parseString(String string) {
        
        final String trimmed = string.trim();
        
        final StringValue stringValue;
        
        if (trimmed.equals("*")) {
            stringValue = new StringValue(true);
        }
        else if (trimmed.isEmpty()) {
            stringValue = new StringValue(false);
        }
        else {
            stringValue = new StringValue(trimmed);
        }
        
        return stringValue;
    }
    
    private static IntegerValue parseInteger(String string) {
        
        final IntegerValue integerValue;
        
        final String trimmed = string.trim();
        
        if (trimmed.equals("*")) {
            integerValue = new IntegerValue(true);
        }
        else if (trimmed.isEmpty()) {
            integerValue = new IntegerValue(false);
        }
        else {
            Integer result = null;
            
            try {
                result = Integer.parseInt(string);
            }
            catch (NumberFormatException ex) {
            }

            if (result == null) {
                integerValue = new IntegerValue(false);
            }
            else {
                integerValue = new IntegerValue(result);
            }
        }
        
        return integerValue;
    }
    
    private XLFD(StringValue foundry, StringValue familyName, StringValue weightName, StringValue slant,
            StringValue setwidthName, StringValue addStyleName, IntegerValue pixelSize, IntegerValue pointSize,
            IntegerValue resolutionX, IntegerValue resolutionY, StringValue spacing, IntegerValue averageWidth,
            StringValue charsetRegistry, StringValue charsetEncoding, String regexPattern) {

        this.foundry = foundry;
        this.familyName = familyName;
        this.weightName = weightName;
        this.slant = slant;
        this.setwidthName = setwidthName;
        this.addStyleName = addStyleName;
        this.pixelSize = pixelSize;
        this.pointSize = pointSize;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.spacing = spacing;
        this.averageWidth = averageWidth;
        this.charsetRegistry = charsetRegistry;
        this.charsetEncoding = charsetEncoding;

        try {
            this.regexPattern = regexPattern != null
                ? FontMatchUtil.getFontMatchGlobPattern(regexPattern)
                : null;
        }
        catch (ValueException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private static StringValue stringValue(String string) {
        
        final StringValue stringValue;
        
        if (string != null) {
            final String trimmed = string.trim();
            
            if (trimmed.equals("*")) {
                throw new IllegalArgumentException();
            }

            stringValue = new StringValue(trimmed);
        }
        else {
            stringValue = new StringValue(false);
        }

        return stringValue;
    }

    private static IntegerValue integerValue(Integer integer) {
        
        return integer != null
                ? new IntegerValue(integer)
                : new IntegerValue(false);
    }

    private XLFD(String foundry, String familyName, String weightName, String slant, String setwidthName,
            String addStyleName, Integer pixelSize, Integer pointSize, Integer resolutionX, Integer resolutionY,
            String spacing, Integer averageWidth, String charsetRegistry, String charsetEncoding,
            String regexPattern) {

        this(
                stringValue(foundry),
                stringValue(familyName),
                stringValue(weightName),
                stringValue(slant),
                stringValue(setwidthName),
                stringValue(addStyleName),
                integerValue(pixelSize),
                integerValue(pointSize),
                integerValue(resolutionX),
                integerValue(resolutionY),
                stringValue(spacing),
                integerValue(averageWidth),
                stringValue(charsetRegistry),
                stringValue(charsetEncoding),
                regexPattern);
    }

    public String getFoundry() {
        return foundry.getValue();
    }

    public String getFamilyName() {
        return familyName.getValue();
    }

    public String getWeightName() {
        return weightName.getValue();
    }

    public String getSlant() {
        return slant.getValue();
    }

    public String getSetwidthName() {
        return setwidthName.getValue();
    }

    public String getAddStyleName() {
        return addStyleName.getValue();
    }

    public Integer getPixelSize() {
        return pixelSize.getValue();
    }

    public Integer getPointSize() {
        return pointSize.getValue();
    }

    public Integer getResolutionX() {
        return resolutionX.getValue();
    }

    public Integer getResolutionY() {
        return resolutionY.getValue();
    }

    public String getSpacing() {
        return spacing.getValue();
    }

    public Integer getAverageWidth() {
        return averageWidth.getValue();
    }

    public String getCharsetRegistry() {
        return charsetRegistry.getValue();
    }

    public String getCharsetEncoding() {
        return charsetEncoding.getValue();
    }

    public String asString() {
        final StringBuilder sb = new StringBuilder(150);
    
        addPart(sb, foundry);
        addPart(sb, familyName);
        addPart(sb, weightName);
        addPart(sb, slant);
        addPart(sb, setwidthName);
        
        addPart(sb, addStyleName);
        
        addPart(sb, pixelSize);
        addPart(sb, pointSize);
        
        addPart(sb, resolutionX);
        addPart(sb, resolutionY);
        
        addPart(sb, spacing);
        
        addPart(sb, averageWidth);
        addPart(sb, charsetRegistry);
        addPart(sb, charsetEncoding);

        return sb.toString();
    }
    
    private static void addPart(StringBuilder sb, Value<?> value) {
        sb.append('-').append(value.asString());
    }

    @Override
    public String toString() {
        return "XLFD [foundry=" + foundry + ", familyName=" + familyName + ", weightName=" + weightName + ", slant="
                + slant + ", setwidthName=" + setwidthName + ", addStyleName=" + addStyleName + ", pixelSize="
                + pixelSize + ", pointSize=" + pointSize + ", resolutionX=" + resolutionX + ", resolutionY="
                + resolutionY + ", spacing=" + spacing + ", averageWidth=" + averageWidth + ", charsetRegistry="
                + charsetRegistry + ", charsetEncoding=" + charsetEncoding + "]";
    }
    
    private static abstract class Value<T> {
        private final boolean isAsterisk;
        final T value;

        abstract boolean isInteger();
        
        abstract String getValueString();
        
        Value(boolean isAsterisk) {
            
            this.isAsterisk = isAsterisk;
            this.value = null;
        }

        Value(T value) {
            Objects.requireNonNull(value);
            
            this.isAsterisk = false;
            this.value = value;
        }
        
        T getValue() {
            if (isAsterisk) {
                throw new IllegalStateException();
            }
            
            return value;
        }
        
        String asString() {
            return isAsterisk ? "*" : (value != null ? getValueString() : "");
        }
    }
    
    private static class IntegerValue extends Value<Integer> {

        IntegerValue(boolean isAsterisk) {
            super(isAsterisk);
        }

        IntegerValue(Integer value) {
            super(value);
        }
        
        @Override
        String getValueString() {
            return String.valueOf(value);
        }

        @Override
        boolean isInteger() {
            return true;
        }
    }

    private static class StringValue extends Value<String> {

        StringValue(boolean isAsterisk) {
            super(isAsterisk);
        }

        StringValue(String value) {
            super(value);
        }
        
        @Override
        String getValueString() {
            
            Objects.requireNonNull(value);
            
            return value;
        }

        @Override
        boolean isInteger() {
            return false;
        }
    }
}
