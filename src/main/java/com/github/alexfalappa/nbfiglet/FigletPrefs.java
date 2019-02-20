/*
 * Copyright 2019 Alessandro Falappa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexfalappa.nbfiglet;

import java.io.IOException;
import java.util.prefs.Preferences;

import org.openide.util.NbPreferences;

import com.github.dtmo.jfiglet.FigFontResources;
import com.github.dtmo.jfiglet.FigletRenderer;

/**
 * Figlet preferences and figlet renderer instance manager.
 *
 * @author Alessandro Falappa
 */
public final class FigletPrefs {

    public static final String PREF_SEL_FONT_IDX = "figletize.font-index";
    public static final String[] fontFileNames = new String[]{"banner.flf", "big.flf", "block.flf", "bubble.flf", "digital.flf", "ivrit.flf", "lean.flf", "mini.flf", "script.flf", "shadow.flf", "slant.flf", "small.flf", "smscript.flf", "smshadow.flf", "smslant.flf", "standard.flf"};
    public static final String[] fontNames = new String[]{"Banner", "Big", "Block", "Bubble", "Digital", "Ivrit", "Lean", "Mini", "Script", "Shadow", "Slant", "Small", "Small Script", "Small Shadow", "Small Slant", "Standard"};
    private static final Preferences nbPrefs = NbPreferences.forModule(FigletPrefs.class);
    private static FigletRenderer renderer;

    public static void storeFontIndex(int idx) {
        nbPrefs.putInt(PREF_SEL_FONT_IDX, idx);
        renderer = null;
    }

    public static int loadFontIndex() {
        return nbPrefs.getInt(FigletPrefs.PREF_SEL_FONT_IDX, fontFileNames.length - 1);
    }

    public static FigletRenderer getCurrentRenderer() throws IOException {
        if (renderer == null) {
            renderer = new FigletRenderer(FigFontResources.loadFigFontResource(FigletPrefs.fontFileNames[loadFontIndex()]));
        }
        return renderer;
    }
}
