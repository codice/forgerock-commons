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
 * Layout documentation for release. The resulting documentation set is found
 * under <code>target/release/</code> by default.
 * <p>
 * You still have some work to do at the top level in docs.forgerock.com before
 * publishing the result.
 *
 * @Checkstyle:ignoreFor 2
 * @goal release
 * @phase site
 */
public class ReleaseBuildMojo extends AbstractBuildMojo {
    /**
     * File system directory for site build.
     *
     * @parameter default-value="${project.build.directory}/release"
     *            property="releaseDirectory"
     * @required
     */
    private File releaseDirectory;

    /**
     * {@inheritDoc}
     */
    public final File getReleaseDirectory() {
        return releaseDirectory;
    }

    /**
     * Version for this release.
     *
     * @parameter property="releaseVersion"
     * @required
     */
    private String releaseVersion;

    /**
     * {@inheritDoc}
     */
    public final String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException {
        Executor exec = new Executor();

        getLog().info("Laying out release...");
        exec.layout();

        String releaseDocDirectory = getReleaseDirectory().getPath()
                + File.separator + getReleaseVersion();

        getLog().info("Adding index.html file...");
        try {
            addIndexHtml(releaseDocDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to copy index.html: "
                    + e.getMessage());
        }

        getLog().info("Renaming .pdfs...");
        renamePDFs(getReleaseVersion(), releaseDocDirectory);

        getLog().info("Replacing CSS...");
        try {
            replaceCSS(releaseDocDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to replace CSS: "
                    + e.getMessage());
        }
    }

    /**
     * Add index.html to redirect according to docs.forgerock.org site policy.
     *
     * @param directory
     *            Directory under which to add the index.html file.
     * @throws IOException
     *             Failed to write the index.html file.
     */
    final void addIndexHtml(final String directory) throws IOException {
        File indexHtml = new File(directory + File.separator + "index.html");
        FileUtils.deleteQuietly(indexHtml);

        String content = IOUtils.toString(
                getClass().getResource("/dfo.index.html"), "UTF-8");
        content = content.replace("PRODUCT", getProjectName().toLowerCase());
        content = content.replace("VERSION", getReleaseVersion());

        FileUtils.writeStringToFile(indexHtml, content, "UTF-8");
    }

    /**
     * Add version number to PDF file names of the form PRODUCT-DOC-NAME.pdf, to
     * make them PRODUCT-VERSION-DOC-NAME.pdf.
     *
     * @param version
     *            Version number to use.
     * @param directory
     *            Directory containing PDFs.
     * @throws MojoExecutionException Failed to rename a PDF file.
     */
    final void renamePDFs(final String version, final String directory)
            throws MojoExecutionException {
        File dir = new File(directory);
        String[] ext = { "pdf" };
        boolean isRecursive = false;
        for (File pdf : FileUtils.listFiles(dir, ext, isRecursive)) {
            String name = pdf.getName().replaceFirst("-", "-" + version + "-");
            if (!pdf.renameTo(new File(pdf.getParent() + File.separator + name))) {
                throw new MojoExecutionException("Failed to rename PDF: " + name);
            }
        }
    }

    /**
     * Replace CSS files for released documentation.
     *
     * @param directory
     *            Directory enclosing HTML documents with CSS.
     * @throws IOException
     *             Could not replace CSS file with new content.
     */
    final void replaceCSS(final String directory) throws IOException {
        File dir = new File(directory);
        String[] ext = { "css" };
        boolean isRecursive = true;
        for (File css : FileUtils.listFiles(dir, ext, isRecursive)) {
            FileUtils.deleteQuietly(css);
            FileUtils.copyURLToFile(getClass().getResource("/dfo.css"), css);
        }
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {
        /**
         * Returns element specifying built documents to copy to the release
         * directory. Currently only HTML and PDF are copied.
         *
         * @return Compound element specifying built documents to copy
         * @throws MojoExecutionException
         *             Something went wrong getting document names.
         */
        private MojoExecutor.Element getResources() throws MojoExecutionException {

            ArrayList<MojoExecutor.Element> r = new ArrayList<MojoExecutor.Element>();

            Set<String> docNames = DocUtils.getDocumentNames(
                    getDocbkxSourceDirectory(), getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }


            List<String> formats = getOutputFormats("html", "pdf");

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

            return element("resources", r.toArray(new Element[0]));
        }

        /**
         * Lay out documentation under <code>target/release</code>.
         *
         * @throws MojoExecutionException
         *             Problem during execution.
         */
        public void layout() throws MojoExecutionException {
            if (releaseDirectory == null) {
                throw new MojoExecutionException("<siteDirectory> must be set.");
            }

            String releaseDocDirectory = FilenameUtils
                    .separatorsToUnix(releaseDirectory.getPath())
                    + "/"
                    + getReleaseVersion();
            executeMojo(
                    plugin(groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(getResourcesVersion())),
                    goal("copy-resources"),
                    configuration(
                            element(name("encoding"), "UTF-8"),
                            element(name("outputDirectory"),
                                    releaseDocDirectory), getResources()),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }
    }
}
