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
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.HtmlUtils;

import java.io.File;
import java.io.IOException;

/**
 * Add {@code .htaccess} file to the site layout.
 */
public class Htaccess {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Htaccess(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Add {@code .htaccess} file to the site layout.
     *
     * @throws MojoExecutionException Failed to copy file.
     */
    public void execute() throws MojoExecutionException {
        final String layoutDir = m.getSiteDirectory().getPath() + File.separator + "doc";
        final File htaccess = new File(m.getBuildDirectory().getPath() + File.separator + ".htaccess");

        FileUtils.deleteQuietly(htaccess);
        try {
            FileUtils.copyURLToFile(getClass().getResource("/.htaccess"), htaccess);
            HtmlUtils.addHtaccess(layoutDir, htaccess);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy .htaccess: " + e.getMessage(), e);
        }
    }
}
