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
 *     Copyright 2013 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Copy common legal notice and front matter to generated sources in preparation
 * to build docs.
 *
 * @Checkstyle:ignoreFor 2
 * @goal boilerplate
 * @phase pre-site
 */
public class CopySharedBuildMojo extends AbstractBuildMojo {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (useSharedContent()) {
            if (!doUseGeneratedSources()) {
                throw new MojoExecutionException("<useGeneratedSources> must"
                        + " be set to true to use common content.");
            }

            getLog().info("Copying common content...");
            copyFiles();
        }
    }

    /**
     * Copy common files.
     *
     * @throws MojoExecutionException
     */
    void copyFiles() throws MojoExecutionException {

        // Are there already generated sources? If not, copy sources over.
        final File outputDir = getDocbkxGeneratedSourceDirectory();
        if (!outputDir.isDirectory()) {
            final File sourceDir = getDocbkxSourceDirectory();
            try {
                FileUtils.copyDirectory(sourceDir, outputDir);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to copy sources", e);
            }
        }

        // Copy common content into the expected locations.
        String prefix = "/common/";
        final String legal = "legal.xml";     // /common/legal.xml
        try {
            final URL legalNotice = getClass().getResource(prefix + legal);
            final File output = new File(outputDir, legal);
            FileUtils.copyURLToFile(legalNotice, output);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy: " + legal, e);
        }

        prefix = prefix + "shared/";
        final String[] shared = {
            "sec-accessing-doc-online.xml",
            "sec-formatting-conventions.xml",
            "sec-interface-stability.xml",
            "sec-joining-the-community.xml",
            "sec-release-levels.xml"
        };
        for (String file : shared) {
            final URL commonFile = getClass().getResource(prefix + file);
            try {
                final File output = new File(outputDir.getPath() + "/shared/", file);
                FileUtils.copyURLToFile(commonFile, output);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to copy: " + file, e);
            }
        }

        prefix = prefix + "images/";
        final String cc = "cc-by-nc-nd.png";
        try {
            final URL ccImage = getClass().getResource(prefix + cc);
            final File output = new File(outputDir.getPath() + "/shared/images/", cc);
            FileUtils.copyURLToFile(ccImage, output);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy: " + cc, e);
        }
    }
}
