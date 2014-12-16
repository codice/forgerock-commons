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
import java.util.HashMap;

@SuppressWarnings("javadoc")
public class FilteredFileUpdaterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void copyTestFiles() throws URISyntaxException, IOException {
        String[] testFileResources = {
            "/unit/utils/ffu/filterme.txt",
            "/unit/utils/ffu/ignore.me",
            "/unit/utils/ffu/unaffected.txt"
        };

        for (String resource : testFileResources) {
            File testFile = new File(getClass().getResource(resource).toURI());
            FileUtils.copyFileToDirectory(testFile, folder.getRoot());
        }
    }

    @Test
    public void shouldOnlyAffectFiltered() throws IOException {
        HashMap<String, String> replacements = new HashMap<String, String>();
        replacements.put("change me", "changed");

        // Match normal directories, and .txt files.
        IOFileFilter dirFilter = FileFilterUtils
                .and(FileFilterUtils.directoryFileFilter(),
                        HiddenFileFilter.VISIBLE);
        IOFileFilter fileFilter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".txt"));
        FileFilter filter = FileFilterUtils.or(dirFilter, fileFilter);

        new FilteredFileUpdater(replacements, filter).update(folder.getRoot());

        File filtered = new File(folder.getRoot(), "filterme.txt");
        assertThat(contentOf(filtered)).isEqualTo("changed");

        File ignored = new File(folder.getRoot(), "ignore.me");
        assertThat(contentOf(ignored)).isEqualTo("change me");

        File unaffected = new File(folder.getRoot(), "unaffected.txt");
        assertThat(contentOf(unaffected)).isEqualTo("unaffected");
    }

    @After
    public void cleanUp() {
        folder.delete();
    }
}
