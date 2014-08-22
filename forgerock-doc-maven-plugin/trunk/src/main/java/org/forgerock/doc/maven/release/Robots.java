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

package org.forgerock.doc.maven.release;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.HtmlUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Remove robots meta tag in release HTML.
 */
public class Robots {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Robots(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Remove robots meta tag in release HTML.
     *
     * @throws MojoExecutionException Failed to remove tags.
     */
    public void execute() throws MojoExecutionException {

        HashMap<String, String> replacements = new HashMap<String, String>();

        try {
            final String robots = IOUtils.toString(
                    getClass().getResourceAsStream("/robots.txt"), "UTF-8");

            replacements.put(robots, ""); // Replace the tag with an empty string.
            HtmlUtils.updateHtml(m.getReleaseVersionPath(), replacements);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
