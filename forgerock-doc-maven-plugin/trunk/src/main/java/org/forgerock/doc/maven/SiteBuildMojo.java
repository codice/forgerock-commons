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
 *     Copyright 2012-2013 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor;

/**
 * Layout built documentation. The resulting documentation set is found under
 * <code>target/site/doc</code>.
 *
 * @Checkstyle:ignoreFor 2
 * @goal layout
 * @phase site
 */
public class SiteBuildMojo extends AbstractBuildMojo {
    /**
     * File system directory for site build.
     *
     * @parameter default-value="${project.build.directory}/site"
     * property="siteDirectory"
     * @required
     */
    private File siteDirectory;

    /**
     * See return.
     * @return {@link #siteDirectory}
     */
    public final File getSiteDirectory() {
        return siteDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException {
        Executor exec = new Executor();
        getLog().info("Laying out site...");
        exec.layout();

        getLog().info("Adding .htaccess file...");
        String layoutDir = getSiteDirectory().getPath() + File.separator
                + "doc";
        File htaccess = new File(getBuildDirectory().getPath() + File.separator
                + ".htaccess");
        FileUtils.deleteQuietly(htaccess);
        try {
            FileUtils.copyURLToFile(getClass().getResource("/.htaccess"),
                    htaccess);
            HTMLUtils.addHtaccess(layoutDir, htaccess);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy .htaccess: "
                    + e.getMessage());
        }

        getLog().info("Add redirect to docs.html under layout directory...");
        try {
            String redirect = IOUtils.toString(
                    getClass().getResourceAsStream("/index.html"), "UTF-8");
            redirect = redirect.replaceAll("PROJECT", getProjectName())
                    .replaceAll("LOWERCASE", getProjectName().toLowerCase());
            File file = new File(getSiteDirectory().getPath() + File.separator
                    + "doc" + File.separator + "index.html");
            FileUtils.write(file, redirect, "UTF-8");
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy redirect file: "
                    + e.getMessage());
        }

        // Test links in document source, and generate a report.
        if (!getRunLinkTester().equalsIgnoreCase("false")) {
            getLog().info("Running linktester...");
            exec.testLinks();
        }
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {
        /**
         * Returns element specifying built documents to copy to the site
         * directory. Man pages are not currently copied anywhere.
         *
         * @return Compound element specifying built documents to copy
         * @throws MojoExecutionException Something went wrong getting document names.
         */
        private MojoExecutor.Element getResources() throws MojoExecutionException {

            ArrayList<MojoExecutor.Element> r = new ArrayList<MojoExecutor.Element>();

            Set<String> docNames = DocUtils.getDocumentNames(
                    getDocbkxSourceDirectory(), getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            List<String> formats = getOutputFormats();

            if (formats.contains("epub")) {
                for (String docName : docNames) {
                    String epubDir = FilenameUtils
                            .separatorsToUnix(getDocbkxOutputDirectory()
                                    .getPath())
                            + "/epub/" + docName;
                    r.add(element(
                            name("resource"),
                            element(name("directory"), epubDir),
                            element(name("includes"),
                                    element(name("include"), "**/*.epub"))));
                }
            }

            if (formats.contains("html")) {
                String htmlDir = FilenameUtils
                        .separatorsToUnix(getDocbkxOutputDirectory().getPath())
                        + "/html/";
                r.add(element(name("resource"),
                        element(name("directory"), htmlDir)));
            }

            if (formats.contains("pdf")) {
                String pdfDir = FilenameUtils
                        .separatorsToUnix(getDocbkxOutputDirectory().getPath())
                        + "/pdf/";
                r.add(element(
                        name("resource"),
                        element(name("directory"), pdfDir),
                        element(name("includes"),
                                element(name("include"), "**/*.pdf"))));
            }

            if (formats.contains("rtf")) {
                String rtfDir = FilenameUtils
                        .separatorsToUnix(getDocbkxOutputDirectory().getPath())
                        + "/rtf/";
                r.add(element(
                        name("resource"),
                        element(name("directory"), rtfDir),
                        element(name("includes"),
                                element(name("include"), "**/*.rtf"))));
            }

            return element("resources", r.toArray(new Element[0]));
        }

        /**
         * Lay out docs in site directory under <code>target/site/doc</code>.
         *
         * @throws MojoExecutionException Problem during execution.
         */
        public void layout() throws MojoExecutionException {
            if (siteDirectory == null) {
                throw new MojoExecutionException("<siteDirectory> must be set.");
            }

            String siteDocDirectory = FilenameUtils
                    .separatorsToUnix(siteDirectory.getPath()) + "/doc";
            executeMojo(
                    plugin(groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(getResourcesVersion())),
                    goal("copy-resources"),
                    configuration(element(name("encoding"), "UTF-8"),
                            element(name("outputDirectory"), siteDocDirectory),
                            getResources()),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }

        /**
         * Test links in source documentation.
         *
         * @throws MojoExecutionException Problem during execution.
         */
        void testLinks() throws MojoExecutionException {
            String include = "**/" + getDocumentSrcName();
            if (doUseGeneratedSources()) {
                include = getDocbkxGeneratedSourceDirectory() + "/" + include;
            }

            final String log = getDocbkxOutputDirectory().getPath() + File.separator
                    + "linktester.err";

            final String jiraUrlPattern =
                    "^https://bugster.forgerock.org/jira/browse/OPEN(AM|ICF|IDM|IG|DJ)-[0-9]{1,4}$";
            final String rfcUrlPattern = "^http://tools.ietf.org/html/rfc[0-9]+$";

            executeMojo(
                    plugin(groupId("org.forgerock.maven.plugins"),
                            artifactId("linktester-maven-plugin"),
                            version(getLinkTesterVersion())),
                    goal("check"),
                    configuration(
                            element(name("includes"),
                                    element(name("include"), include)),
                            element(name("validating"), "true"),
                            element(name("skipUrls"), getSkipLinkCheck()),
                            element(name("xIncludeAware"), "true"),
                            element(name("failOnError"), "false"),
                            element(name("outputFile"), log),
                            element(name("skipUrlPatterns"),
                                    element(name("skipUrlPattern"), jiraUrlPattern),
                                    element(name("skipUrlPattern"), rfcUrlPattern))),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }
    }
}
