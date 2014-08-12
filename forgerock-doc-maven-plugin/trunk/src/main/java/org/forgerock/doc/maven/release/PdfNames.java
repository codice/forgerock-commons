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

/**
 * Rename PDF files in the release layout.
 */
public class PdfNames {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public PdfNames(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Rename PDF files in the release layout.
     *
     * @throws MojoExecutionException Failed to rename files.
     */
    public void execute() throws MojoExecutionException {
        final File dir = new File(m.getReleaseVersionPath());
        final String[] ext = {"pdf"};

        for (File pdf : FileUtils.listFiles(dir, ext, false)) { // Not recursive
            String name = pdf.getName().replaceFirst("-", "-" + m.getReleaseVersion() + "-");
            if (!pdf.renameTo(new File(pdf.getParent() + File.separator + name))) {
                throw new MojoExecutionException("Failed to rename PDF: " + name);
            }
        }
    }
}
