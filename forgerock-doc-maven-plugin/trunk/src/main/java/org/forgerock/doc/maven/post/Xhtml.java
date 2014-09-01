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

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.SyntaxHighlighterCopier;

import java.io.File;
import java.io.IOException;

/**
 * XHTML post-processor.
 */
public class Xhtml {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Xhtml(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Add SyntaxHighlighter files for each XHTML document.
     *
     * @throws MojoExecutionException Failed to post-process XHTML.
     */
    public void execute() throws MojoExecutionException {

        String[] outputDirectories = new String[m.getDocNames().size()];

        int i = 0;
        for (final String docName : m.getDocNames()) {

            // Example: ${project.build.directory}/docbkx/xhtml/my-book
            outputDirectories[i] =
                    m.getDocbkxOutputDirectory().getPath()
                            + File.separator + "xhtml"
                            + File.separator + docName;
            ++i;
        }

        SyntaxHighlighterCopier copier =
                new SyntaxHighlighterCopier(outputDirectories);
        try {
            copier.copy();
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to copy files: " + e.getMessage(), e);
        }
    }
}
