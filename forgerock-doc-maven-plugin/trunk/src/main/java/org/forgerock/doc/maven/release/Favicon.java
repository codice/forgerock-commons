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

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.HtmlUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Fix favicon links in release HTML.
 */
public class Favicon {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Favicon(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Fix favicon links in release HTML.
     *
     * @throws MojoExecutionException Failed to fix links.
     */
    public void execute() throws MojoExecutionException {

        HashMap<String, String> replacements = new HashMap<String, String>();
        final String oldFaviconLink = m.getFaviconLink();
        final String newFaviconLink = m.getReleaseFaviconLink();

        if (!oldFaviconLink.equalsIgnoreCase(newFaviconLink)) {
            replacements.put(oldFaviconLink, newFaviconLink);
            try {
                HtmlUtils.updateHtml(m.getReleaseVersionPath(), replacements);
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }
}
