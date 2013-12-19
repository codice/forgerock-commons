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

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.twdata.maven.mojoexecutor.MojoExecutor;

/**
 * Implementation to filter core documentation sources, replacing Maven
 * properties such as {@code ${myProperty}} with their values.
 * <p>
 * This class uses the property {@code filteredDocbkxSourceDirectory}, which
 * sets the output directory for filtered source files.
 * <p>
 * If used, this should be the first execution of the doc build plugin.
 *
 * @Checkstyle:ignoreFor 2
 * @goal filter
 * @phase pre-site
 */
public class FilterPreSiteBuildMojo extends AbstractBuildMojo {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (!getDocbkxGeneratedSourceDirectory().exists()) {
            throw new MojoExecutionException("<docbkxGeneratedSourceDirectory>"
                + " must exist before you filter resources.\n"
                + "Copy common content with the boilerplate goal first.");
        }

        // The Executor is what actually calls other plugins.
        Executor exec = new Executor();

        getLog().info("Filtering DocBook XML sources to resolve Maven properties...");
        exec.filter();
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Filter DocBook XML source files.
         * @throws MojoExecutionException
         */
        void filter() throws MojoExecutionException {
            final String filteredOutputDirectory =
                    FilenameUtils.separatorsToUnix(
                            getFilteredDocbkxSourceDirectory().getPath());
            final String sourceDirectory =
                    FilenameUtils.separatorsToUnix(
                            getDocbkxGeneratedSourceDirectory().getPath());

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(getResourcesVersion())),
                    goal("copy-resources"),
                    configuration(
                            element(name("outputDirectory"), filteredOutputDirectory),
                            element(name("resources"),
                                    element(name("resource"),
                                            element(name("directory"), sourceDirectory),
                                            element(name("filtering"), "true"),
                                            element(name("includes"),
                                                    element(name("include"), "**/*.xml"))),
                                     element(name("resource"),
                                            element(name("directory"), sourceDirectory),
                                            element(name("filtering"), "false"),
                                            element(name("excludes"),
                                                    element(name("exclude"), "**/*.xml"))))),
                    executionEnvironment(
                            getProject(),
                            getSession(),
                            getPluginManager()));
        }
    }
}
