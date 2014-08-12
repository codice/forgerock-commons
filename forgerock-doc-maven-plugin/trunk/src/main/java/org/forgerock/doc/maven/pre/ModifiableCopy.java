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
 *     Copyright 2012-2014 ForgeRock AS
 *
 */

package org.forgerock.doc.maven.pre;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;

import java.io.File;
import java.io.IOException;

/**
 * Make a modifiable copy of the documentation source files,
 * rather than working directly on the original source files.
 */
public class ModifiableCopy {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public ModifiableCopy(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Make a modifiable copy of the original DocBook XML source files.
     *
     * @throws MojoExecutionException Failed to copy sources.
     */
    public void execute() throws MojoExecutionException {

        final File sourceDir = m.getDocbkxSourceDirectory();
        final File outputDir = m.getDocbkxModifiableSourcesDirectory();

        try {
            FileUtils.copyDirectory(sourceDir, outputDir);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy sources", e);
        }
    }
}
