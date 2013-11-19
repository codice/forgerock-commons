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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.twdata.maven.mojoexecutor.MojoExecutor;

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
            ensureGeneratedSources();
            Executor exec = new Executor();
            exec.unpack();
        }
    }

    /**
     * Copy source files if there are no generated sources, yet.
     *
     * @throws MojoExecutionException
     */
    void ensureGeneratedSources() throws MojoExecutionException {

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
    }

    /**
     * GroupId of the common content to use.
     *
     * @parameter default-value="org.forgerock.commons" property="commonContentGroupId"
     * @required
     */
    private String commonContentGroupId;

    /**
     * Gets the group ID of the common content artifact to use.
     * @return commonContentGroupId
     */
    public String getCommonContentGroupId() {
        return commonContentGroupId;
    }

    /**
     * ArtifactId of the common content to use.
     *
     * @parameter default-value="forgerock-doc-common-content" property="commonContentArtifactId"
     * @required
     */
    private String commonContentArtifactId;

    /**
     * Gets the common content artifact to use.
     * @return commonContentArtifactId
     */
    public String getCommonContentArtifactId() {
        return commonContentArtifactId;
    }

    /**
     * Version of the common content artifact to use.
     *
     * @parameter default-value="1.0.0-SNAPSHOT" property="commonContentVersion"
     * @required
     */
    private String commonContentVersion;

    /**
     * Gets the version of the common content artifact to use.
     * @return commonContentVersion
     */
    public String getCommonContentVersion() {
        return commonContentVersion;
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Unpack common content.
         *
         * @throws MojoExecutionException
         */
        void unpack() throws MojoExecutionException {
            final String outputDir = FilenameUtils.separatorsToUnix(
                    getDocbkxGeneratedSourceDirectory().getPath());

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version("2.8")),
                    goal("unpack"),
                    configuration(
                            element("artifactItems",
                                    element("artifactItem",
                                            element("groupId", getCommonContentGroupId()),
                                            element("artifactId", getCommonContentArtifactId()),
                                            element("version", getCommonContentVersion()),
                                            element("type", "jar"),
                                            element("overWrite", "true"),
                                            element("outputDirectory", outputDir),
                                            element("includes", "**/*.*")))),
                    executionEnvironment(
                            getProject(),
                            getSession(),
                            getPluginManager()));

        }
    }
}
