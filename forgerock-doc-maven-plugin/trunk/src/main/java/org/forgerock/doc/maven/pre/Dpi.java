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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.PngUtils;

import java.io.File;
import java.io.IOException;

/**
 * Set DPI on .png images in the modifiable copy of the sources.
 *
 * <p>
 *
 * This class transforms the .png images in place.
 */
public class Dpi {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Dpi(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Set DPI on .png images in the modifiable copy of the sources.
     * Default: 160 DPI.
     *
     * @throws MojoExecutionException Failed to edit image file.
     */
    public void execute() throws MojoExecutionException {

        try {

            for (File image : FileUtils.listFiles(
                    m.getDocbkxModifiableSourcesDirectory(),
                    new WildcardFileFilter("*.png"),
                    TrueFileFilter.INSTANCE)) {
                PngUtils.setSafeDpi(image, m.getMaxImageHeightInInches());
            }

        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
