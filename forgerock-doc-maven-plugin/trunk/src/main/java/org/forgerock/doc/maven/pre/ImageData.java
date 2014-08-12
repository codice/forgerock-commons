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
 *     Copyright 2013-2014 ForgeRock AS
 *
 */

package org.forgerock.doc.maven.pre;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.ImageDataTransformer;

import java.io.FileFilter;
import java.io.IOException;

/**
 * Edit {@code &lt;imagedata&gt;} elements in DocBook XML sources.
 */
public class ImageData {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public ImageData(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Edit {@code &lt;imagedata&gt;} elements in the copy of DocBook XML sources.
     *
     * @throws MojoExecutionException Failed to update an XML file.
     */
    public void execute() throws MojoExecutionException {
        // Match normal directories, and XML files.
        IOFileFilter dirFilter = FileFilterUtils
                .and(FileFilterUtils.directoryFileFilter(),
                        HiddenFileFilter.VISIBLE);
        IOFileFilter fileFilter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".xml"));
        FileFilter filterToMatch = FileFilterUtils.or(dirFilter, fileFilter);

        // Update XML files.
        ImageDataTransformer idt = new ImageDataTransformer(filterToMatch);
        try {
            idt.update(m.getDocbkxModifiableSourcesDirectory());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
