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
 * Unpack branding elements.
 * The branding is unpacked from a separate artifact.
 */
public class Branding {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Branding(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Augment the modifiable copy of DocBook XML sources with branding elements.
     *
     * @throws MojoExecutionException Failed to unpack branding.
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
         * Unpack branding elements from the branding artifact.
         *
         * @throws MojoExecutionException Failed to unpack branding.
         */
        public void unpack() throws MojoExecutionException {
            final String outputDir = m.path(m.getBuildDirectory());

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-dependency-plugin"),
                            version("2.8")),
                    goal("unpack"),
                    configuration(
                            element("artifactItems",
                                    element("artifactItem",
                                            element("groupId", m.getBrandingGroupId()),
                                            element("artifactId", m.getBrandingArtifactId()),
                                            element("version", m.getBrandingVersion()),
                                            element("type", "jar"),
                                            element("overWrite", "true"),
                                            element("outputDirectory", outputDir),
                                            element("includes", "**/*.*")))),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }
    }
}
