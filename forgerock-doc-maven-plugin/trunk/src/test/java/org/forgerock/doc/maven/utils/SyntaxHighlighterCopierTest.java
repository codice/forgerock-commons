/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If applicable, add the following below this MPL 2.0 HEADER, replacing
 * the fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *     Portions Copyright [yyyy] [name of copyright owner]
 *
 *     Copyright 2014 ForgeRock AS
 *
 */

package org.forgerock.doc.maven.utils;

import static org.assertj.core.api.Assertions.*;

import org.apache.tools.ant.DirectoryScanner;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

@SuppressWarnings("javadoc")
public class SyntaxHighlighterCopierTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldCopySyntaxHighlighterFiles() throws IOException {
        String[] outputDirectories = { folder.getRoot().getPath() };
        SyntaxHighlighterCopier copier = new SyntaxHighlighterCopier(outputDirectories);
        copier.copy();

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(folder.getRoot());
        String[] includes = { "**/*.css", "**/*.js" };
        scanner.setIncludes(includes);
        scanner.scan();

        String[] shFiles = scanner.getIncludedFiles();
        assertThat(shFiles).contains("sh/css/shCore.css", "sh/js/shAll.js");
    }

    @After
    public void tearDown() {
        folder.delete();
    }
}
