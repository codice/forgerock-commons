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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("javadoc")
public class HtmlUtilsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File bookDir;
    private final String bookName = "book.html";
    private File bookFile;

    @Before
    public void setUp() throws IOException {
        bookDir = folder.newFolder("test");
        bookFile = new File(bookDir, bookName);
        if (!bookFile.createNewFile()) {
            throw new IOException("setUp failed to create " + bookFile.getPath());
        }
    }

    @Test
    public void shouldAddHtAccess() throws IOException {
        File htAccess = File.createTempFile("prefix", "ext");

        HtmlUtils.addHtaccess(folder.getRoot().getPath(), htAccess);
        assertThat(new File(folder.getRoot(), htAccess.getName())).exists();

        htAccess.delete();
    }

    @Test
    public void xmlCssShouldExist() throws IOException {
        File cssFile = folder.newFile("test.css");
        FileUtils.writeStringToFile(cssFile, "body { rounded-corners: true }");

        HtmlUtils.addCustomCss(cssFile, folder.getRoot(), bookName);
        assertThat(new File(bookDir, cssFile.getName() + ".xml")).exists();
    }

    @Test
    public void shouldReplaceContent() throws IOException {
        FileUtils.writeStringToFile(bookFile, "<p>Replace me</p>");
        HashMap<String, String> replacements = new HashMap<String, String>();
        replacements.put("Replace me", "Replaced");

        List<File> list = HtmlUtils.updateHtml(bookDir.getPath(), replacements);
        assertThat(list.size()).isEqualTo(1);

        for (File file : list) {
            assertThat(contentOf(file)).isEqualTo("<p>Replaced</p>");
        }
    }

    @Test
    public void shouldAddDotDotToHref() throws IOException {
        FileUtils.writeStringToFile(bookFile, "<a href=\"../resources\">");
        HtmlUtils.fixResourceLinks(bookDir.getPath(), "resources");
        assertThat(bookFile).hasContent("<a href=\"../../resources\">");

        FileUtils.writeStringToFile(bookFile, "<a href='../resources'>");
        HtmlUtils.fixResourceLinks(bookDir.getPath(), "resources");
        assertThat(bookFile).hasContent("<a href='../../resources'>");
    }

    @After
    public void tearDown() {
        folder.delete();
    }

}
