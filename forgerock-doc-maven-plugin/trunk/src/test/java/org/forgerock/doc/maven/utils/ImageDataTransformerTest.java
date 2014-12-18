/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 ForgeRock AS
 */

package org.forgerock.doc.maven.utils;

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;

@SuppressWarnings("javadoc")
public class ImageDataTransformerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException, URISyntaxException {
        File testFile = new File(getClass().getResource("/unit/utils/idt/chapter.xml").toURI());
        FileUtils.copyFileToDirectory(testFile, folder.getRoot());
    }

    @Test
    public void shouldAddImageAttributes() throws IOException {
        IOFileFilter dirFilter = FileFilterUtils
                .and(FileFilterUtils.directoryFileFilter(),
                        HiddenFileFilter.VISIBLE);
        IOFileFilter fileFilter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".xml"));
        FileFilter filterToMatch = FileFilterUtils.or(dirFilter, fileFilter);

        new ImageDataTransformer(filterToMatch).update(folder.getRoot());

        /*
            This causes the following messages at present.

            Compiler warnings:
              line 38: Attribute 'align' outside of element.
              line 39: Attribute 'scalefit' outside of element.
              line 40: Attribute 'width' outside of element.
              line 41: Attribute 'contentdepth' outside of element.

            It would be nice to know what those warnings mean,
            but the output seems to be okay for further processing.
         */

        File out = new File(folder.getRoot(), "chapter.xml");
        String expectedElement =
                "<db:imagedata xmlns:db=\"http://docbook.org/ns/docbook\""
                        + " fileref=\"images/an-image.png\" format=\"PNG\""
                        + " align=\"center\" scalefit=\"1\" width=\"100%\""
                        + " contentdepth=\"100%\"/>";

        assertThat(contentOf(out)).contains(expectedElement);
    }

    @After
    public void tearDown() {
        folder.delete();
    }
}
