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
import static org.mockito.Mockito.*;

import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("javadoc")
public class ImageCopierTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final String srcName   = "book.xml";
    private final String imageName = "image.jpg";
    private File bookDir;
    private File subDir;
    private AbstractDocbkxMojo mojo;

    @Before
    public void setUp() throws IOException {
        // folder/book/book.xml
        File srcDir = folder.newFolder("book");
        File srcFile = new File(srcDir, srcName);
        if (!srcFile.createNewFile()) {
            throw new IOException("setUp failed to create " + srcFile.getPath());
        }

        // folder/book/images/image.jpg
        File imageDir = new File(srcDir, "images");
        if (imageDir.mkdir()) {
            File image = new File(imageDir, imageName);
            if (!image.createNewFile()) {
                throw new IOException("setUp failed to create " + image.getPath());
            }
        }

        // folder/html/book/book.html
        File outDir = folder.newFolder("html");
        bookDir = new File(outDir, "book");
        String outName = "book.html";
        if (bookDir.mkdir()) {
            File outFile = new File(bookDir, outName);
            if (!outFile.createNewFile()) {
                throw new IOException("setUp failed to create " + outFile.getPath());
            }
        }

        // folder/html/book/sub/book.html
        subDir = new File(bookDir, "sub");
        if (subDir.mkdir()) {
            File subOutFile = new File(subDir, outName);
            if (!subOutFile.createNewFile()) {
                throw new IOException("setUp failed to create " + subOutFile.getPath());
            }
        }

        mojo = mock(AbstractDocbkxMojo.class);
        when(mojo.getDocumentSrcName()).thenReturn(srcName);
        when(mojo.getDocbkxModifiableSourcesDirectory()).thenReturn(folder.getRoot());
        when(mojo.getDocbkxOutputDirectory()).thenReturn(folder.getRoot());
    }

    @Test
    public void shouldCopyOneLevel() throws IOException {
        ImageCopier.copyImages("html", null, mojo);

        File imageDir = new File(bookDir, "images");
        File image    = new File(imageDir, imageName);
        assertThat(image).exists();
    }

    @Test
    public void shouldCopyToSubDir() throws IOException {
        ImageCopier.copyImages("html", subDir.getName(), srcName, folder.getRoot(), folder.getRoot());

        File imageDir = new File(subDir, "images");
        File image    = new File(imageDir, imageName);
        assertThat(image).exists();
    }

    @After
    public void tearDown() {
        folder.delete();
    }

}
