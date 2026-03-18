package com.novusforge.astrum.common.infiniminer;

/**
 * Port of Infiniminer's Defines class.
 * Originally defined in InfiniminerShared/SharedConstants.cs
 */
public class InfiniminerConstants {
    public static final String INFINIMINER_VERSION = "v1.5";
    public static final int GROUND_LEVEL = 8;

    public static final String DEATH_BY_LAVA = "WAS INCINERATED BY LAVA!";
    public static final String DEATH_BY_ELEC = "WAS ELECTROCUTED!";
    public static final String DEATH_BY_EXPL = "WAS KILLED IN AN EXPLOSION!";
    public static final String DEATH_BY_FALL = "WAS KILLED BY GRAVITY!";
    public static final String DEATH_BY_MISS = "WAS KILLED BY MISADVENTURE!";
    public static final String DEATH_BY_SUIC = "HAS COMMITED PIXELCIDE!";

    // Matte-Vibrant Palette
    public static final int COLOR_FERROUS = 0xFF707070; // Matte Gray
    public static final int COLOR_CUPROUS = 0xFFD46A24; // Matte Orange/Copper
    public static final int COLOR_AURUM   = 0xFFFFD700; // Vibrant Gold
    public static final int COLOR_ARGENTUM = 0xFFC0C0C0; // Silver
    public static final int COLOR_DIRT    = 0xFF8B4513; // Matte Brown
    public static final int COLOR_ROCK    = 0xFF404040; // Dark Matte Gray
    public static final int COLOR_LAVA    = 0xFFFF4500; // Vibrant Orange-Red

    public static final int IM_BLUE = 0xFF4A90E2; // Matte Blue
    public static final int IM_RED  = 0xFFE35A5A; // Matte Red

    public static String sanitize(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 32 && c <= 126) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
