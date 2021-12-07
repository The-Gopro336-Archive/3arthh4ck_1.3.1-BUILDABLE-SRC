/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.text;

public enum TextColor {
    None{

        @Override
        public String getColor() {
            return "";
        }
    }
    ,
    Black{

        @Override
        public String getColor() {
            return TextColor.BLACK;
        }
    }
    ,
    White{

        @Override
        public String getColor() {
            return TextColor.WHITE;
        }
    }
    ,
    DarkBlue{

        @Override
        public String getColor() {
            return TextColor.DARK_BLUE;
        }
    }
    ,
    DarkGreen{

        @Override
        public String getColor() {
            return TextColor.DARK_GREEN;
        }
    }
    ,
    DarkAqua{

        @Override
        public String getColor() {
            return TextColor.DARK_AQUA;
        }
    }
    ,
    DarkRed{

        @Override
        public String getColor() {
            return TextColor.DARK_RED;
        }
    }
    ,
    DarkPurple{

        @Override
        public String getColor() {
            return TextColor.DARK_PURPLE;
        }
    }
    ,
    Gold{

        @Override
        public String getColor() {
            return TextColor.GOLD;
        }
    }
    ,
    Gray{

        @Override
        public String getColor() {
            return TextColor.GRAY;
        }
    }
    ,
    DarkGray{

        @Override
        public String getColor() {
            return TextColor.DARK_GRAY;
        }
    }
    ,
    Blue{

        @Override
        public String getColor() {
            return TextColor.BLUE;
        }
    }
    ,
    Green{

        @Override
        public String getColor() {
            return TextColor.GREEN;
        }
    }
    ,
    Aqua{

        @Override
        public String getColor() {
            return TextColor.AQUA;
        }
    }
    ,
    Red{

        @Override
        public String getColor() {
            return TextColor.RED;
        }
    }
    ,
    LightPurple{

        @Override
        public String getColor() {
            return TextColor.LIGHT_PURPLE;
        }
    }
    ,
    Yellow{

        @Override
        public String getColor() {
            return TextColor.YELLOW;
        }
    }
    ,
    Obfuscated{

        @Override
        public String getColor() {
            return TextColor.OBFUSCATED;
        }
    }
    ,
    Bold{

        @Override
        public String getColor() {
            return TextColor.BOLD;
        }
    }
    ,
    Strike{

        @Override
        public String getColor() {
            return TextColor.STRIKE;
        }
    }
    ,
    Underline{

        @Override
        public String getColor() {
            return TextColor.UNDERLINE;
        }
    }
    ,
    Italic{

        @Override
        public String getColor() {
            return TextColor.ITALIC;
        }
    }
    ,
    Reset{

        @Override
        public String getColor() {
            return TextColor.RESET;
        }
    }
    ,
    Rainbow{

        @Override
        public String getColor() {
            return TextColor.RAINBOW;
        }
    }
    ,
    RainbowHorizontal{

        @Override
        public String getColor() {
            return TextColor.RAINBOW_PLUS;
        }
    }
    ,
    RainbowVertical{

        @Override
        public String getColor() {
            return TextColor.RAINBOW_MINUS;
        }
    };

    public static final char SECTIONSIGN = '\u00a7';
    public static final String BLACK = "\u00a70";
    public static final String DARK_BLUE = "\u00a71";
    public static final String DARK_GREEN = "\u00a72";
    public static final String DARK_AQUA = "\u00a73";
    public static final String DARK_RED = "\u00a74";
    public static final String DARK_PURPLE = "\u00a75";
    public static final String GOLD = "\u00a76";
    public static final String GRAY = "\u00a77";
    public static final String DARK_GRAY = "\u00a78";
    public static final String BLUE = "\u00a79";
    public static final String GREEN = "\u00a7a";
    public static final String AQUA = "\u00a7b";
    public static final String RED = "\u00a7c";
    public static final String LIGHT_PURPLE = "\u00a7d";
    public static final String YELLOW = "\u00a7e";
    public static final String WHITE = "\u00a7f";
    public static final String OBFUSCATED = "\u00a7k";
    public static final String BOLD = "\u00a7l";
    public static final String STRIKE = "\u00a7m";
    public static final String UNDERLINE = "\u00a7n";
    public static final String ITALIC = "\u00a7o";
    public static final String RESET = "\u00a7r";
    public static final String CUSTOM = "\u00a7z";
    public static final String RAINBOW = "\u00a7y";
    public static final String RAINBOW_PLUS = "\u00a7+";
    public static final String RAINBOW_MINUS = "\u00a7-";

    public abstract String getColor();
}

