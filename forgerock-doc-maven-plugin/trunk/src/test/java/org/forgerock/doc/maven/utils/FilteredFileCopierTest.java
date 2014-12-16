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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("javadoc")
public class FilteredFileCopierTest {

    @Rule
    public TemporaryFolder sourceFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder destinationFolder = new TemporaryFolder();

    @Test
    public void shouldCopyOnlyOthers() throws IOException {
        File doCopy = sourceFolder.newFile("do.cpy");
        File doNotCopy = sourceFolder.newFile("do.not");

        FilteredFileCopier.copyOthers("not", sourceFolder.getRoot(), destinationFolder.getRoot());

        File copied = new File(destinationFolder.getRoot(), "do.cpy");
        File notCopied = new File(destinationFolder.getRoot(), "do.not");

        assertThat(copied).exists();
        assertThat(notCopied).doesNotExist();

        sourceFolder.delete();
        destinationFolder.delete();
    }
}
