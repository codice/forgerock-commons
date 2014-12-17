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

package org.forgerock.doc.maven.pre;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;

/**
 * Augment the modifiable copy DocBook XML sources with common content.
 * The common content is unpacked from a separate artifact.
 */
public class CommonContent {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * The Executor to run the dependency plugin.
     */
    private final Executor executor;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public CommonContent(final AbstractDocbkxMojo mojo) {
        m = mojo;
        this.executor = new Executor();
    }

    /**
     * Augment the modifiable copy of DocBook XML sources with common content.
     *
     * @throws MojoExecutionException Failed to unpack common content.
     */
    public void execute() throws MojoExecutionException {

        // As shown in https://github.com/markcraig/unpack-test
        // the maven-dependency-plugin overWrite* options
        // do not prevent existing files from being overwritten.

        File sharedContentUnpackDir;

        if (m.doOverwriteProjectFilesWithSharedContent()) { // Do overwrite
            sharedContentUnpackDir = m.getDocbkxModifiableSourcesDirectory();
            executor.unpack(m.path(sharedContentUnpackDir));
        } else {                                            // Do not overwrite
            try {
                // Unpack without overwriting.
                sharedContentUnpackDir = createTemporaryDirectory();
                executor.unpack(m.path(sharedContentUnpackDir));

                // Now overwrite what was unpacked with the existing files,
                // and then replace our copy with the updated unpacked files.
                FileUtils.copyDirectory(m.getDocbkxModifiableSourcesDirectory(), sharedContentUnpackDir);
                FileUtils.deleteDirectory(m.getDocbkxModifiableSourcesDirectory());
                FileUtils.moveDirectory(sharedContentUnpackDir, m.getDocbkxModifiableSourcesDirectory());
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to unpack common content.", e);
            }
        }
    }

    /**
     * Try to create a writable temporary directory with a unique name.
     *
     * @return The temporary directory.
     * @throws IOException Failed to create the directory.
     */
    private File createTemporaryDirectory() throws IOException {
        File temporaryDirectory = new File(
                m.getBuildDirectory(), Long.toString(System.nanoTime()));

        if (!temporaryDirectory.mkdir() && temporaryDirectory.canWrite()) {
            throw new IOException("Failed to create temporary directory: "
                    + temporaryDirectory.getAbsolutePath());
        }

        return temporaryDirectory;
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Unpack common content from the common content artifact.
         *
         * @param outputDir Where to unpack common content
         * @throws MojoExecutionException Failed to unpack common content.
         */
        public void unpack(final String outputDir) throws MojoExecutionException {

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version(m.getMavenDependencyVersion())),
                    goal("unpack"),
                    configuration(
                            element("artifactItems",
                                    element("artifactItem",
                                            element("groupId", m.getCommonContentGroupId()),
                                            element("artifactId", m.getCommonContentArtifactId()),
                                            element("version", m.getCommonContentVersion()),
                                            element("type", "jar"),
                                            element("overWrite", "true"),
                                            element("outputDirectory", outputDir),
                                            element("includes", "**/*.*")))),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }
    }
}
