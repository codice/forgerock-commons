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

package org.forgerock.doc.maven.site;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;

import java.io.File;
import java.io.IOException;

/**
 * Add file to redirect {@code /doc/index.html} to {@code /docs.html}.
 */
public class Redirect {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Redirect(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Add file to redirect {@code /doc/index.html} to {@code /docs.html}.
     *
     * @throws MojoExecutionException Failed to write file.
     */
    public void execute() throws MojoExecutionException {
        try {
            File file = FileUtils.getFile(m.getSiteDirectory(), "doc", "index.html");
            if (!file.exists()) {
                String redirect = IOUtils.toString(getClass().getResourceAsStream("/index.html"), "UTF-8");
                redirect = redirect.replaceAll("PROJECT", m.getProjectName())
                        .replaceAll("LOWERCASE", m.getProjectName().toLowerCase());
                FileUtils.write(file, redirect, "UTF-8");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy redirect file: " + e.getMessage(), e);
        }
    }
}
