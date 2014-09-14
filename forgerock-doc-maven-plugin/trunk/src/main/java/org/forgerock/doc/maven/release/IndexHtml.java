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
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;

import java.io.File;
import java.io.IOException;

/**
 * Add an index.html file to the release layout.
 */
public class IndexHtml {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public IndexHtml(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Add an index.html file to the release layout.
     *
     * @throws MojoExecutionException Failed to copy file.
     */
    public void execute() throws MojoExecutionException {
        if (!m.keepCustomIndexHtml()) {
            final File indexHtml = new File(m.getReleaseVersionPath(), "index.html");
            FileUtils.deleteQuietly(indexHtml);

            try {
                String content = IOUtils.toString(getClass().getResource("/dfo.index.html"), "UTF-8");
                content = content.replace("PRODUCT", m.getProjectName().toLowerCase());
                content = content.replace("VERSION", m.getReleaseVersion());

                FileUtils.writeStringToFile(indexHtml, content, "UTF-8");
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }
}
