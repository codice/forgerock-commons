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

package org.forgerock.doc.maven.site;

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lay out built documents,
 * by default under {@code ${project.build.directory}/site/doc/}.
 */
public class Layout {

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
    public Layout(final AbstractDocbkxMojo mojo) {
        m = mojo;
        this.executor = new Executor();
    }

    /**
     * Lay out built documents.
     *
     * @throws MojoExecutionException Failed to layout site.
     */
    public void execute() throws MojoExecutionException {
        executor.layout();
    }

    /**
     * Get element specifying built documents to copy to the site directory.
     *
     * <p>
     *
     * Man pages are not currently copied anywhere.
     *
     * <p>
     *
     * Webhelp is handled separately.
     *
     * @return Compound element specifying built documents to copy.
     * @throws MojoExecutionException Something went wrong getting document names.
     */
    private MojoExecutor.Element getResources() throws MojoExecutionException {

        ArrayList<MojoExecutor.Element> r = new ArrayList<MojoExecutor.Element>();

        final List<String> formats = m.getFormats();
        final String outputDir = m.path(m.getDocbkxOutputDirectory());

        if (formats.contains("epub")) {
            r.add(element(name("resource"),
                    element(name("directory"), outputDir + "/epub/"),
                    element(name("includes"),
                            element(name("include"), "**/*.epub"))));
        }

        if (formats.contains("html")) {
            r.add(element(name("resource"),
                    element(name("directory"), outputDir + "/html/")));
        }

        // Man pages are not currently copied anywhere.

        if (formats.contains("pdf")) {
            r.add(element(name("resource"),
                    element(name("directory"), outputDir + "/pdf/"),
                    element(name("includes"),
                            element(name("include"), "**/*.pdf"))));
        }

        if (formats.contains("rtf")) {
            r.add(element(name("resource"),
                    element(name("directory"), outputDir + "/rtf/"),
                    element(name("includes"),
                            element(name("include"), "**/*.rtf"))));
        }

        // Webhelp is handled separately.

        if (formats.contains("xhtml5")) {
            r.add(element(name("resource"),
                    element(name("directory"), outputDir + "/xhtml/")));
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

            final String siteDocDirectory = m.path(m.getSiteDirectory()) + "/doc";

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(m.getResourcesVersion())),
                    goal("copy-resources"),
                    configuration(
                            element(name("encoding"), "UTF-8"),
                            element(name("outputDirectory"), siteDocDirectory),
                            getResources()),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));

            // The webhelp directory needs to be copied in its entirety
            // to avoid overwriting other HTML.

            if (m.getFormats().contains("webhelp")) {

                // 2.0.15 does not allow <webhelpBaseDir> to be set,
                // so the output location is hard-coded for now.
                final String webHelpDir = m.path(m.getDocbkxOutputDirectory()) + "/webhelp";

                executeMojo(
                        plugin(
                                groupId("org.apache.maven.plugins"),
                                artifactId("maven-resources-plugin"),
                                version(m.getResourcesVersion())),
                        goal("copy-resources"),
                        configuration(
                                element(name("encoding"), "UTF-8"),
                                element(name("outputDirectory"), siteDocDirectory + "/webhelp"),
                                element(name("resources"),
                                        element(name("resource"),
                                                element(name("directory"), webHelpDir),
                                                element(name("excludes"),
                                                        element(name("exclude"), "**/*.target.db"))))),
                        executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
            }

            // Optionally copy an entire directory of arbitrary resources, too.
            if (m.doCopyResourceFiles() && m.getResourcesDirectory().exists()) {
                try {
                    FileUtils.copyDirectoryToDirectory(m.getResourcesDirectory(),
                            new File(m.getSiteDirectory(), "doc"));
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to copy resources", e);
                }
            }
        }
    }
}
