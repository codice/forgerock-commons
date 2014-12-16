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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@SuppressWarnings("javadoc")
public class NameUtilsTest {

    @Test
    public void shouldSuccessfullyAdaptCorrectNames() {
        assertThat(NameUtils.renameDoc("commons", "doc", "", ""))
                .isEqualTo("Commons-Doc");
        assertThat(NameUtils.renameDoc("commons", "doc", "13.0.0", ""))
                .isEqualTo("Commons-13.0.0-Doc");
        assertThat(NameUtils.renameDoc("commons", "doc", "1.4.0.1", ""))
                .isEqualTo("Commons-1.4.0.1-Doc");
        assertThat(NameUtils.renameDoc("commons", "doc", "2.5.0-Xpress1", ""))
                .isEqualTo("Commons-2.5.0-Xpress1-Doc");
        assertThat(NameUtils.renameDoc("commons", "doc", "1.0.0-SNAPSHOT", ""))
                .isEqualTo("Commons-1.0.0-SNAPSHOT-Doc");
        assertThat(NameUtils.renameDoc("commons", "doc", "", "ext"))
                .isEqualTo("Commons-Doc.ext");
        assertThat(NameUtils.renameDoc("commons", "doc", "13.0.0", "ext"))
                .isEqualTo("Commons-13.0.0-Doc.ext");
        assertThat(NameUtils.renameDoc("commons", "doc", "1.4.0.1", "ext"))
                .isEqualTo("Commons-1.4.0.1-Doc.ext");
        assertThat(NameUtils.renameDoc("commons", "doc", "2.5.0-Xpress1", "ext"))
                .isEqualTo("Commons-2.5.0-Xpress1-Doc.ext");
        assertThat(NameUtils.renameDoc("commons", "doc", "1.0.0-SNAPSHOT", "ext"))
                .isEqualTo("Commons-1.0.0-SNAPSHOT-Doc.ext");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "", ""))
                .isEqualTo("Commons-Secret007-Guide");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "13.0.0", ""))
                .isEqualTo("Commons-13.0.0-Secret007-Guide");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "1.4.0.1", ""))
                .isEqualTo("Commons-1.4.0.1-Secret007-Guide");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "2.5.0-Xpress1", ""))
                .isEqualTo("Commons-2.5.0-Xpress1-Secret007-Guide");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "1.0.0-SNAPSHOT", ""))
                .isEqualTo("Commons-1.0.0-SNAPSHOT-Secret007-Guide");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "", "ext"))
                .isEqualTo("Commons-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "13.0.0", "ext"))
                .isEqualTo("Commons-13.0.0-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "1.4.0.1", "ext"))
                .isEqualTo("Commons-1.4.0.1-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "2.5.0-Xpress1", "ext"))
                .isEqualTo("Commons-2.5.0-Xpress1-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("commons", "secret007-guide", "1.0.0-SNAPSHOT", "ext"))
                .isEqualTo("Commons-1.0.0-SNAPSHOT-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "", ""))
                .isEqualTo("ForgeRock-Doc");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "13.0.0", ""))
                .isEqualTo("ForgeRock-13.0.0-Doc");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "1.4.0.1", ""))
                .isEqualTo("ForgeRock-1.4.0.1-Doc");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "2.5.0-Xpress1", ""))
                .isEqualTo("ForgeRock-2.5.0-Xpress1-Doc");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "1.0.0-SNAPSHOT", ""))
                .isEqualTo("ForgeRock-1.0.0-SNAPSHOT-Doc");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "", "ext"))
                .isEqualTo("ForgeRock-Doc.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "13.0.0", "ext"))
                .isEqualTo("ForgeRock-13.0.0-Doc.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "1.4.0.1", "ext"))
                .isEqualTo("ForgeRock-1.4.0.1-Doc.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "2.5.0-Xpress1", "ext"))
                .isEqualTo("ForgeRock-2.5.0-Xpress1-Doc.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "doc", "1.0.0-SNAPSHOT", "ext"))
                .isEqualTo("ForgeRock-1.0.0-SNAPSHOT-Doc.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "", ""))
                .isEqualTo("ForgeRock-Secret007-Guide");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "13.0.0", ""))
                .isEqualTo("ForgeRock-13.0.0-Secret007-Guide");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "1.4.0.1", ""))
                .isEqualTo("ForgeRock-1.4.0.1-Secret007-Guide");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "2.5.0-Xpress1", ""))
                .isEqualTo("ForgeRock-2.5.0-Xpress1-Secret007-Guide");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "1.0.0-SNAPSHOT", ""))
                .isEqualTo("ForgeRock-1.0.0-SNAPSHOT-Secret007-Guide");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "", "ext"))
                .isEqualTo("ForgeRock-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "13.0.0", "ext"))
                .isEqualTo("ForgeRock-13.0.0-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "1.4.0.1", "ext"))
                .isEqualTo("ForgeRock-1.4.0.1-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "2.5.0-Xpress1", "ext"))
                .isEqualTo("ForgeRock-2.5.0-Xpress1-Secret007-Guide.ext");
        assertThat(NameUtils.renameDoc("ForgeRock", "secret007-guide", "1.0.0-SNAPSHOT", "ext"))
                .isEqualTo("ForgeRock-1.0.0-SNAPSHOT-Secret007-Guide.ext");
    }

    @Test
    public void shouldReturnEmptyName() {
        assertThat(NameUtils.renameDoc("commons", "", "version", "ext")).isEqualTo("");
    }

    @Test
    public void shouldReturnEmptyVersion() {
        assertThat(NameUtils.renameDoc("", "guide", "version", "ext")).isEqualTo("Guide.ext");
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File doc;

    @Before
    public void setUp() throws IOException {
        File parent = new File(folder.getRoot(), "admin-guide");

        if (parent.mkdir()) {
            doc = new File(parent, "source");
            doc.createNewFile();
        }

        File other = new File(folder.getRoot(), "resources");

        if (other.mkdir()) {
            new File(other, "resource").createNewFile();
        }
    }

    @Test
    public void shouldFindDocumentNames() {
        Set<String> docNames = NameUtils.getDocumentNames(folder.getRoot(), "source");
        assertThat(docNames).containsExactly("admin-guide");
    }

    @Test
    public void shouldRenameDocument() throws IOException {
        NameUtils.renameDocument(doc, "admin-guide", "project");
        assertThat(new File(doc.getParent(), "Project-Admin-Guide")).exists();
    }

    @After
    public void tearDown() {
        folder.delete();
    }
}
