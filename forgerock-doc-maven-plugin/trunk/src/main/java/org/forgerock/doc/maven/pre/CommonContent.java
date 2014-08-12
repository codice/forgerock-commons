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

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

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
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public CommonContent(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Augment the modifiable copy of DocBook XML sources with common content.
     *
     * @throws MojoExecutionException Failed to unpack common content.
     */
    public void execute() throws MojoExecutionException {
        Executor executor = new Executor();
        executor.unpack();
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Unpack common content from the common content artifact.
         *
         * @throws MojoExecutionException Failed to unpack common content.
         */
        public void unpack() throws MojoExecutionException {
            final String outputDir = m.path(m.getDocbkxModifiableSourcesDirectory());

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version("2.8")),
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
