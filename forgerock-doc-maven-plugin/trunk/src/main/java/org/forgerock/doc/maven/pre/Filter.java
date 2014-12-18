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
 * Copyright 2013-2014 ForgeRock AS
 */

package org.forgerock.doc.maven.pre;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;

/**
 * Apply Maven resource filtering to the modifiable copy of DocBook XML sources,
 * replacing Maven properties such as {@code ${myProperty}} with their values.
 */
public class Filter {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * The Executor to run the resources plugin.
     */
    private final Executor executor;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Filter(final AbstractDocbkxMojo mojo) {
        m = mojo;
        this.executor = new Executor();
        sourceDirectory = m.path(m.getDocbkxModifiableSourcesDirectory());
        tempOutputDirectory = new File(m.getBuildDirectory(), "docbkx-filtered");
        filteredOutputDirectory = m.path(tempOutputDirectory);
    }

    // Filter the sources in the modifiable copy.
    private final String sourceDirectory;

    // Filter to a temporary directory.
    private final File tempOutputDirectory;
    private final String filteredOutputDirectory;

    /**
     * Apply Maven resource filtering to the copy of DocBook XML sources.
     *
     * @throws MojoExecutionException Failed to filter a file.
     */
    public void execute() throws MojoExecutionException {

        // Filter to a temporary directory...
        executor.filter();

        // ...and then overwrite the modifiable sources with filtered files.
        try {
            FileUtils.copyDirectory(tempOutputDirectory, m.getDocbkxModifiableSourcesDirectory());
            FileUtils.deleteDirectory(tempOutputDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Filter DocBook XML source files.
         *
         * @throws MojoExecutionException Failed to filter a file.
         */
        void filter() throws MojoExecutionException {

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(m.getMavenResourcesVersion())),
                    goal("copy-resources"),
                    configuration(
                            element(name("outputDirectory"), filteredOutputDirectory),
                            element(name("resources"),
                                    element(name("resource"),
                                            element(name("directory"), sourceDirectory),
                                            element(name("filtering"), "true"),
                                            element(name("includes"),
                                                    element(name("include"), "**/*.json"),
                                                    element(name("include"), "**/*.txt"),
                                                    element(name("include"), "**/*.xml"))),
                                    element(name("resource"),
                                            element(name("directory"), sourceDirectory),
                                            element(name("filtering"), "false"),
                                            element(name("excludes"),
                                                    element(name("exclude"), "**/*.json"),
                                                    element(name("exclude"), "**/*.txt"),
                                                    element(name("exclude"), "**/*.xml"))))),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }
    }
}
