package bfinterpreter;

import java.awt.*;
import java.io.InputStream;

public class Fonts {
    public static final Font DEFAULT = load();

    private static Font load() {
        try (InputStream is = Fonts.class.getResourceAsStream("/JetBrains_Mono/JetBrainsMono-VariableFont_wght.ttf")) {
            assert is != null;
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 12);
        }
    }
}
