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
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Zip release documents if configured to do so.
 *
 * <p>
 *
 * This zips the release layout only on one level,
 * and does not handle assembly of multiple zips
 * into a single documentation set .zip.
 */
public class Zip {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * The Executor to run the assembly plugin.
     */
    private final Executor executor;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Zip(final AbstractDocbkxMojo mojo) {
        m = mojo;
        this.executor = new Executor();
    }

    /**
     * Zip release documents.
     *
     * @throws MojoExecutionException Failed to zip documents.
     */
    public void execute() throws MojoExecutionException {
        executor.zip();
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Zip release documents.
         *
         * @throws MojoExecutionException Failed to zip documents.
         */
        public void zip() throws MojoExecutionException {

            if (!m.doBuildReleaseZip()) {
                return;
            }

            final URL resource = getClass().getResource("/zip.xml");
            final File assembly = new File(m.getBuildDirectory(), "assembly.xml");

            try {
                FileUtils.copyURLToFile(resource, assembly);
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }


            final String finalName = m.getProjectName() + "-" + m.getReleaseVersion();

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-assembly-plugin"),
                            version("2.4")),
                    goal("single"),
                    configuration(
                            element(name("finalName"), finalName),
                            element(name("descriptors"),
                                    element(name("descriptor"), m.path(assembly)))),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }
    }
}
