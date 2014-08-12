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

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Lay out built documents,
 * by default under {@code ${project.build.directory}/release/version}.
 *
 * <p>
 *
 * Currently only HTML and PDF are released.
 */
public class Layout {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Layout(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Lay out built documents.
     *
     * @throws MojoExecutionException Failed to layout site.
     */
    public void execute() throws MojoExecutionException {
        Executor executor = new Executor();
        executor.layout();
    }

    /**
     * Get element specifying built documents to copy to the release directory.
     *
     * <p>
     *
     * Currently only HTML and PDF are released.
     *
     * @return Compound element specifying built documents to copy.
     * @throws MojoExecutionException Something went wrong getting document names.
     */
    private MojoExecutor.Element getResources() throws MojoExecutionException {

        ArrayList<MojoExecutor.Element> r = new ArrayList<MojoExecutor.Element>();
        final List<String> formats = m.getFormats();
        final String outputDir = m.path(m.getDocbkxOutputDirectory());

        if (formats.contains("html")) {
            r.add(element(name("resource"),
                    element(name("directory"), outputDir + "/html/")));
        }

        if (formats.contains("pdf")) {
            r.add(element(name("resource"),
                    element(name("directory"), outputDir + "/pdf/"),
                    element(name("includes"),
                            element(name("include"), "**/*.pdf"))));
        }

        return element("resources", r.toArray(new MojoExecutor.Element[r.size()]));
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Lay out built documents.
         *
         * @throws MojoExecutionException Failed to lay out documents.
         */
        public void layout() throws MojoExecutionException {

            executeMojo(
                    plugin(groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(m.getResourcesVersion())),
                    goal("copy-resources"),
                    configuration(
                            element(name("encoding"), "UTF-8"),
                            element(name("outputDirectory"),
                                    m.path(m.getReleaseDirectory()) + "/" + m.getReleaseVersion()),
                            getResources()),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }
    }
}
