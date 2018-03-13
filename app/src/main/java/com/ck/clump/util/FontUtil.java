package com.ck.clump.util;

import android.graphics.Typeface;

import com.ck.clump.App;

/**
 * Created by Nhat on 10/4/17.
 */

public final class FontUtil {

    public static Typeface getTypeface(FontStyle fontStyle) {
        return Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/" + fontStyle.getFont());
    }

    public enum FontStyle {
        PT_SAN_BOLD("ptSansBold.ttf"), PT_SAN_BOLD_ITALIC("ptSansBoldItalic.ttf"), PT_SAN_ITALIC("ptSansItalic.ttf"), PT_SAN_REGULAR("ptSansRegular.ttf");
        private final String font;

        FontStyle(String font) {
            this.font = font;
        }

        public String getFont() {
            return font;
        }
    }
}
