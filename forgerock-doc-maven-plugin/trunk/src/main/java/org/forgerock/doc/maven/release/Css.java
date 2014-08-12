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

package org.forgerock.doc.maven.release;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;

import java.io.File;
import java.io.IOException;

/**
 * Replace CSS in release HTML.
 */
public class Css {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Css(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Replace CSS in release HTML.
     *
     * @throws MojoExecutionException Failed to replace CSS.
     */
    public void execute() throws MojoExecutionException {

        final File newCss = m.getReleaseCss();
        final File dir = new File(m.getReleaseVersionPath());
        final String[] ext = {"css"};
        final boolean isRecursive = true;

        for (File oldCss : FileUtils.listFiles(dir, ext, isRecursive)) {
            if (m.getPreSiteCss().getName().equals(oldCss.getName())) {
                try {
                    FileUtils.copyFile(newCss, oldCss);
                } catch (IOException e) {
                    throw new MojoExecutionException(e.getMessage(), e);
                }
            }
        }
    }
}
