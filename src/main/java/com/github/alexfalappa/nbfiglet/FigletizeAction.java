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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.netbeans.api.editor.EditorRegistry;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.text.NbDocument;
import org.openide.util.NbBundle.Messages;

import com.github.dtmo.jfiglet.FigletRenderer;

/**
 * Action that rewrites selected text or current line with FIGlet fonts.
 *
 * @author Alessandro Falappa
 */
@ActionID(
        category = "Edit",
        id = "com.github.alexfalappa.nbfiglet.FigletAction"
)
@ActionRegistration(
        displayName = "#CTL_FigletAction"
)
@ActionReferences({
    @ActionReference(path = "Editors/Popup", position = 4010),
    @ActionReference(path = "Shortcuts", name = "DA-F")
})
@Messages("CTL_FigletAction=Figletize")
public final class FigletizeAction implements ActionListener {

    private final EditorCookie context;

    public FigletizeAction(EditorCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final FigletRenderer figRend;
        try {
            figRend = FigletPrefs.getCurrentRenderer();
        } catch (IOException ex) {
            return;
        }
        final JTextComponent tc = EditorRegistry.lastFocusedComponent();
        final StyledDocument doc = context.getDocument();
        int lineNo = NbDocument.findLineNumber(doc, tc.getCaretPosition());
        // find offsets of line where cursor is
        int lineStart = NbDocument.findLineOffset(doc, lineNo);
        int lineEnd;
        try {
            lineEnd = NbDocument.findLineOffset(doc, lineNo + 1);
        } catch (IndexOutOfBoundsException ex) {
            lineEnd = doc.getLength() - 1;
        }
        // retrieve selection and its boundaries
        String text = tc.getSelectedText();
        int selectionStart = tc.getSelectionStart();
        int selectionLen = tc.getSelectionEnd() - selectionStart;
        // if no selection operate on entire line
        final boolean hasSelection = text != null;
        if (!hasSelection) {
            try {
                selectionStart = lineStart;
                selectionLen = lineEnd - 1 - lineStart;
                text = doc.getText(selectionStart, selectionLen);
            } catch (BadLocationException ex) {
                return;
            }
        }
        String figletized = figRend.renderText(text);
        // if there was a selection repeat text from beginning of line to beginning of selection on every line
        if (hasSelection) {
            try {
                String linePrefix = doc.getText(lineStart, selectionStart - lineStart);
                figletized = figletized.replace("\n", "\n".concat(linePrefix));
            } catch (BadLocationException ex) {
            }
        }
        // run two modifications atomically
        NbDocument.runAtomic(doc, new DocReplacer(doc, selectionStart, selectionLen, figletized));
    }
}

class DocReplacer implements Runnable {

    private final StyledDocument doc;
    private final int start;
    private final int len;
    private final String replacement;

    public DocReplacer(StyledDocument doc, int start, int len, String replacement) {
        this.doc = doc;
        this.start = start;
        this.len = len;
        this.replacement = replacement;
    }

    @Override
    public void run() {
        try {
            doc.remove(start, len);
            doc.insertString(start, replacement, null);
        } catch (BadLocationException ex) {
        }
    }

}
