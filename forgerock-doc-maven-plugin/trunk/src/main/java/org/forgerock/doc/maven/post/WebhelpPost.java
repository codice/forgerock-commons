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

package org.forgerock.doc.maven.post;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.HtmlUtils;

import java.io.File;
import java.io.IOException;

/**
 * Webhelp post-processor for both single-page and chunked HTML formats.
 */
public class WebhelpPost {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public WebhelpPost(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Post-processes HTML formats.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException Failed to post-process HTML.
     */
    public void execute() throws MojoExecutionException {

        final File webhelpDir = new File(m.getDocbkxOutputDirectory(), "webhelp");

        if (m.doCopyResourceFiles() && m.getResourcesDirectory().exists()) {

            final String baseName = FilenameUtils.getBaseName(m.getResourcesDirectory().getPath());

            try {
                HtmlUtils.fixResourceLinks(webhelpDir.getPath(), baseName);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to update resource links", e);
            }
        }
    }
}
