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
 *     Copyright 2012 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * AbstractMojo implementation for building core documentation from <a
 * href="http://www.docbook.org/tdg51/en/html/docbook.html">DocBook XML</a>
 * using <a href="http://code.google.com/p/docbkx-tools/">docbkx-tools</a>.
 */
abstract class AbstractBuildMojo extends AbstractMojo {
    /**
     * Docbkx Tools plugin version to use. Executions seem to hit an NPE when
     * the version is not specified.
     *
     * @parameter default-value="2.0.14" expression="${docbkxVersion}
     * @required
     */
    private String docbkxVersion;

    /**
     * Maven resources plugin version to use. Executions seem to hit an NPE when
     * the version is not specified.
     *
     * @parameter default-value="2.5" expression="${resourcesVersion}
     * @required
     */
    private String resourcesVersion;

    /**
     * ForgeRock linktester plugin version to use. Executions seem to hit an NPE
     * when the version is not specified.
     *
     * @parameter default-value="1.0.0" expression="${linkTesterVersion}
     * @required
     */
    private String linkTesterVersion;

    /**
     * Whether the ForgeRock linktester plugin should skip checking that
     * external URLs are valid. See the {@code skipUrls} parameter of the <a
     * href="https://github.com/aldaris/docbook-linktester/">linktester
     * plugin</a>.
     *
     * @parameter default-value="false" expression="${skipLinkCheck}"
     */
    private String skipLinkCheck;

    /**
     * Whether to run the ForgeRock linktester plugin. You only need to run
     * the linktester plugin from the top level of a project, not the modules.
     *
     * @parameter default-value="true" expression="${runLinkTester}"
     */
    private String runLinkTester;

    /**
     * Short name of the project, such as OpenAM, OpenDJ, OpenIDM.
     *
     * @parameter expression="${projectName}"
     * @required
     */
    private String projectName;

    /**
     * Google Analytics identifier for the project.
     *
     * @parameter expression="${googleAnalyticsId}"
     * @required
     */
    private String googleAnalyticsId;

    /**
     * Do not process these formats. Choices include: epub, html, man, pdf, rtf.
     * Do not set both excludes and includes in the same configuration.
     *
     * @parameter
     */
    private List<String> excludes;

    /**
     * Process only this format. Choices include: epub, html, man, pdf, rtf.
     * Do not set both excludes and includes in the same configuration.
     *
     * @parameter expression="${include}"
     */
    private String include;

    /**
     * Base directory for DocBook XML source files.
     *
     * @parameter default-value="${basedir}/src/main/docbkx"
     *            expression="${docbkxSourceDirectory}"
     * @required
     */
    private File docbkxSourceDirectory;

    /**
     * Base directory for built documentation.
     *
     * @parameter default-value="${project.build.directory}/docbkx"
     *            expression="${docbkxOutputDirectory}"
     * @required
     */
    private File docbkxOutputDirectory;

    /**
     * Target directory for this plugin.
     *
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private File buildDirectory;

    /**
     * The Maven Project Object.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The Maven Session Object.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * The Maven PluginManager Object.
     *
     * @component
     * @required
     */
    private BuildPluginManager pluginManager;

    /**
     * Top-level DocBook documents included in the documentation set such as
     * books, articles, and references share a common entry point, which is a
     * file having the name specified by this element.
     * <p>
     * For example, if your documentation set has Release Notes, an Installation
     * Guide, a Developer's Guide, and a Reference, your source layout under the
     * base DocBook XML source directory might look like this, assuming you use
     * the default file name, <code>index.xml</code>.
     *
     * <pre>
     * docbkx/
     *  dev-guide/
     *   index.xml
     *   ...other files...
     *  install-guide/
     *   index.xml
     *   ...other files...
     *  reference/
     *   index.xml
     *   ...other files...
     *  release-notes/
     *   index.xml
     *   ...other files...
     *  shared/
     *   ...other files...
     * </pre>
     *
     * The <code>...other files...</code> can have whatever names you want, as
     * long as the name does not conflict with the file name you set here.
     *
     * @parameter default-value="index.xml" expression="${documentSrcName}"
     * @required
     */
    private String documentSrcName;

    /**
     * {@inheritDoc}
     */
    public String getDocbkxVersion() {
        return docbkxVersion;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourcesVersion() {
        return resourcesVersion;
    }

    /**
     * {@inheritDoc}
     */
    public String getLinkTesterVersion() {
        return linkTesterVersion;
    }

    /**
     * {@inheritDoc}
     */
    public String getSkipLinkCheck() {
        return skipLinkCheck;
    }

    /**
     * {@inheritDoc}
     */
    public void setSkipLinkCheck(String doLinkCheck) {
        this.skipLinkCheck = doLinkCheck;
    }

    /**
     * {@inheritDoc}
     */
    public String getRunLinkTester() {
        return runLinkTester;
    }

    /**
     * {@inheritDoc}
     */
    public void setRunLinkTester(String runLinkTester) {
        this.runLinkTester = runLinkTester;
    }

    /**
     * {@inheritDoc}
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * {@inheritDoc}
     */
    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getExcludes() {
        return excludes;
    }

    /**
     * {@inheritDoc}
     */
    public void setExcludes(final List<String> excludedFormats) {
        this.excludes = excludedFormats;
    }

    /**
     * {@inheritDoc}
     */
    public String getInclude() {
        return include;
    }

    /**
     * {@inheritDoc}
     */
    public void setInclude(String includedFormat) {
        this.include = includedFormat;
    }

    /**
     * {@inheritDoc}
     */
    public File getDocbkxSourceDirectory() {
        return docbkxSourceDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public File getDocbkxOutputDirectory() {
        return docbkxOutputDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public File getBuildDirectory() {
        return buildDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public MavenProject getProject() {
        return project;
    }

    /**
     * {@inheritDoc}
     */
    public MavenSession getSession() {
        return session;
    }

    /**
     * {@inheritDoc}
     */
    public BuildPluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * {@inheritDoc}
     */
    public String getDocumentSrcName() {
        return documentSrcName;
    }

    /**
     * Return a list of output formats to generate. If no defaults are
     * specified, then the default list of formats includes epub, html, man,
     * pdf, rtf.
     *
     * @param defaults
     *            (Restricted) list of formats to consider. Set this to limit
     *            the list of output formats. Formats are passed on to the
     *            plugin as is.
     * @return List of output formats.
     */
    public List<String> getOutputFormats(final String... defaults)
            throws MojoExecutionException {
        List<String> formats = Arrays.asList("epub", "html", "man", "pdf", "rtf");
        if (defaults.length != 0) {                      // Restrict list.
            formats = Arrays.asList(defaults);
        }

        if (getExcludes() == null) {
            setExcludes(new ArrayList<String>());
        }

        if (getInclude() == null) {
            setInclude("");
        }

        if (!getExcludes().isEmpty() && !getInclude().equals("")) {
            throw new MojoExecutionException("Do not set both <excludes> and"
                    + "<include> in the same configuration.");

        } else if (!getExcludes().isEmpty()) {          // Exclude formats.
            for (String format : getExcludes()) {
                formats.remove(format);
            }
        } else if (formats.contains(getInclude())) {    // Include one format.
            List<String> includes = new ArrayList<String>();
            includes.add(getInclude());
            formats = includes;
        }
        return formats;
    }
}
