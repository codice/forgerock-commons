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

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test links in pre-processed copy of the sources.
 *
 * <p>
 *
 * Errors are written by default to {@code ${project.build.directory}/docbkx/linktester.err}.
 * The test does not fail on error.
 */
public class LinkTest {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * The Executor to run the linktester plugin.
     */
    private final Executor executor;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public LinkTest(final AbstractDocbkxMojo mojo) {
        m = mojo;
        this.executor = new Executor();
    }

    /**
     * Test links in pre-processed copy of the sources.
     *
     * @throws MojoExecutionException Failed to complete link tests.
     */
    public void execute() throws MojoExecutionException {
        executor.test();
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Run link tester.
         *
         * @throws MojoExecutionException Failed to run link tester.
         */
        public void test() throws MojoExecutionException {

            if (m.runLinkTester().equalsIgnoreCase("false")) {
                return;
            }

            // Check only preprocessed sources.
            // This works with a relative path, not with an absolute path.
            final String buildDirectory = getName(m.getBuildDirectory());
            final String include = buildDirectory + "/**/" + m.getDocumentSrcName();

            final String log = m.path(new File(m.getDocbkxOutputDirectory(), "linktester.err"));

            // The list of URL patterns to skip can be extended by the configuration.
            MojoExecutor.Element skipUrlPatterns = getSkipUrlPatterns();

            executeMojo(
                    plugin(
                            groupId("org.forgerock.maven.plugins"),
                            artifactId("linktester-maven-plugin"),
                            version(m.getLinkTesterVersion())),
                    goal("check"),
                    configuration(
                            element(name("includes"),
                                    element(name("include"), include)),
                            element(name("validating"), "true"),
                            element(name("skipUrls"), m.skipLinkCheck()),
                            element(name("xIncludeAware"), "true"),
                            element(name("failOnError"), "false"),
                            element(name("outputFile"), log),
                            skipUrlPatterns),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }

        /**
         * Return the name of a file or directory relative to its parent.
         *
         * @param file  The file or directory.
         * @return      The name of the file or directory relative to its parent.
         */
        private String getName(File file) {
            return file.getPath().replace(file.getParent() + File.separator, "");
        }

        /**
         * Return the URL patterns to skip, which can be extended by configuration.
         *
         * @return      The URL patterns to skip.
         */
        private MojoExecutor.Element getSkipUrlPatterns() {

            final MojoExecutor.Element[] defaultPatterns = {
                element(name("skipUrlPattern"), // ForgeRock JIRA
                        "^https://bugster.forgerock.org/jira/browse/.+$"),
                element(name("skipUrlPattern"), // RFCs
                        "^http://tools.ietf.org/html/rfc[0-9]+$"),
                element(name("skipUrlPattern"), // localhost
                        "^https?://localhost.*$"),
                element(name("skipUrlPattern"), // example (see RFC 2606)
                        "^https?://[^/]*example.*$"),
                element(name("skipUrlPattern"), // relative URLs
                        "^\\.\\./.*$")
            };

            // The full list includes both default patterns and also configured patterns.
            final ArrayList<Element> patterns = new ArrayList<Element>();
            patterns.addAll(Arrays.asList(defaultPatterns));
            for (String pattern: m.getSkipUrlPatterns()) {
                patterns.add(element("skipUrlPattern", pattern));
            }

            return element(name("skipUrlPatterns"), patterns.toArray(new Element[patterns.size()]));
        }
    }
}
