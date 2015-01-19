/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2012-2015 ForgeRock AS
 */

package org.forgerock.doc.maven.post;

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.BootstrapCopier;

import java.io.File;
import java.io.IOException;

/**
 * HTML post-processor for both single-page and chunked HTML formats.
 */
public class Bootstrap {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Bootstrap(final AbstractDocbkxMojo mojo) {
        m = mojo;

        outputDirectories = new String[1];
        outputDirectories[0] = "";
     /*   outputDirectories[1] = File.separator + FilenameUtils.getBaseName(m
                .getDocumentSrcName());  */
    }

    /**
     * Post-processes HTML formats.
     *
     * @throws MojoExecutionException Failed to post-process HTML.
     */
    public void execute() throws MojoExecutionException {

        // Add HtmlForBootstrap files.
        final File htmlDir = new File(m.getDocbkxOutputDirectory(),
                "bootstrap");

        String[] outputDirectories = new String[m.getDocNames().size()];

        int i = 0;
        for (final String docName : m.getDocNames()) {

            final File docDir = new File(htmlDir, docName);

            // Example:
            // ${project.build.directory}/docbkx/html/my-book
            outputDirectories[i] = docDir.getPath();
            ++i;

        }

        BootstrapCopier copier =
                new BootstrapCopier(outputDirectories);
        try {
            copier.copy();
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to copy files: " + e.getMessage(), e);
        }

    }

    /**
     * Directories where scripts and CSS are to be added.
     */
    private String[] outputDirectories;
}
