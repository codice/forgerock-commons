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
     * @parameter default-value="2.0.14" property="docbkxVersion"
     * @required
     */
    private String docbkxVersion;

    /**
     * Maven resources plugin version to use. Executions seem to hit an NPE when
     * the version is not specified.
     *
     * @parameter default-value="2.5" property="resourcesVersion"
     * @required
     */
    private String resourcesVersion;

    /**
     * JCite version to use for code citations.
     *
     * @parameter default-value="1.13.0" property="jCiteVersion"
     * @required
     */
    private String jCiteVersion;

    /**
     * ForgeRock link tester plugin version to use. Executions seem to hit an
     * NPE when the version is not specified.
     *
     * @parameter default-value="1.2.0" property="linkTesterVersion"
     * @required
     */
    private String linkTesterVersion;

    /**
     * Whether the ForgeRock link tester plugin should skip checking that
     * external URLs are valid. See the {@code skipUrls} parameter of the <a
     * href="https://github.com/aldaris/docbook-linktester/">link tester
     * plugin</a>.
     *
     * @parameter default-value="false" property="skipLinkCheck"
     */
    private String skipLinkCheck;

    /**
     * Whether to run the ForgeRock link tester plugin. You only need to run the
     * link tester plugin from the top level of a project, not the modules.
     *
     * @parameter default-value="true" property="runLinkTester"
     */
    private String runLinkTester;

    /**
     * Whether to use common legal notice and front matter to build docs.
     * Use of common content relies on {@code useGeneratedSources} being
     * {@code true} as the common content overwrites files of the same
     * name in the project.
     *
     * @parameter default-value="true" property="useSharedContent"
     */
    private boolean useSharedContent;

    /**
     * Short name of the project, such as OpenAM, OpenDJ, OpenIDM.
     *
     * @parameter property="projectName"
     * @required
     */
    private String projectName;

    /**
     * Google Analytics identifier for the project.
     *
     * @parameter default-value="UA-23412190-14" property="googleAnalyticsId"
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
     * @parameter property="include"
     */
    private String include;

    /**
     * Base directory for DocBook XML source files.
     *
     * @parameter default-value="${basedir}/src/main/docbkx"
     * property="docbkxSourceDirectory"
     * @required
     */
    private File docbkxSourceDirectory;

    /**
     * Base directory for processed DocBook XML source files, relative to the
     * build directory.
     *
     * @parameter default-value="generated-docbkx"
     * property="docbkxGeneratedSourceDirectory"
     * @required
     */
    private String docbkxGeneratedSourceDirectory;

    /**
     * Base directory for filtered DocBook XML source files, relative to the
     * build directory.
     *
     * @parameter default-value="docbkx-filtered"
     * property="filteredDocbkxSourceDirectory"
     * @required
     */
    private String filteredDocbkxSourceDirectory;

    /**
     * Whether to use filtered sources.
     * @parameter default-value="true" property="useFilteredSources"
     * @required
     */
    private boolean useFilteredSources;

    /**
     * Whether to use generated sources.
     *
     * @parameter default-value="true" property="useGeneratedSources"
     * @required
     */
    private boolean useGeneratedSources;

    /**
     * Base directory for built documentation, relative to the build directory.
     *
     * @parameter default-value="docbkx"
     * property="docbkxOutputDirectory"
     * @required
     */
    private String docbkxOutputDirectory;

    /**
     * Build directory for this plugin, usually {@code ${project.build.directory}.
     *
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private File buildDirectory;

    /**
     * The Maven Project Object.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The Maven Session Object.
     *
     * @parameter property="session"
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
     * <p/>
     * For example, if your documentation set has Release Notes, an Installation
     * Guide, a Developer's Guide, and a Reference, your source layout under the
     * base DocBook XML source directory might look like this, assuming you use
     * the default file name, <code>index.xml</code>.
     * <p/>
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
     * <p/>
     * The <code>...other files...</code> can have whatever names you want, as
     * long as the name does not conflict with the file name you set here.
     *
     * @parameter default-value="index.xml" property="documentSrcName"
     * @required
     */
    private String documentSrcName;

    /**
     * Favicon link element for the pre-site version of the HTML.
     *
     * @parameter
     * default-value="<link rel=\"shortcut icon\" href=\"http://forgerock.org/favicon.ico\">"
     * property="faviconLink"
     * @required
     */
    private String faviconLink;

    /**
     * Get the favicon link element for the pre-site version of the HTML.
     * @return The link element
     */
    public final String getFaviconLink() {
        return faviconLink;
    }

    /**
     * GroupId of the branding to use.
     *
     * @parameter default-value="org.forgerock.commons" property="brandingGroupId"
     * @required
     */
    private String brandingGroupId;

    /**
     * Gets the group ID of the branding artifact to use.
     * @return brandingGroupId
     */
    public String getBrandingGroupId() {
        return brandingGroupId;
    }

    /**
     * ArtifactId of the branding to use.
     *
     * @parameter default-value="forgerock-doc-default-branding" property="brandingArtifactId"
     * @required
     */
    private String brandingArtifactId;

    /**
     * Gets the branding artifact to use.
     * @return brandingArtifactId
     */
    public String getBrandingArtifactId() {
        return brandingArtifactId;
    }

    /**
     * Version of the branding artifact to use.
     *
     * @parameter default-value="1.0.1" property="brandingVersion"
     * @required
     */
    private String brandingVersion;

    /**
     * Gets the version of the branding artifact to use.
     * @return brandingVersion
     */
    public String getBrandingVersion() {
        return brandingVersion;
    }

    /**
     * Docbkx Tools plugin version to use.
     *
     * @return {@link #docbkxVersion}
     */
    public String getDocbkxVersion() {
        return docbkxVersion;
    }

    /**
     * Maven resources plugin version to use.
     *
     * @return {@link #resourcesVersion}
     */
    public String getResourcesVersion() {
        return resourcesVersion;
    }

    /**
     * JCite plugin version to use.
     *
     * @return {@link #jCiteVersion}
     */
    public String getJCiteVersion() {
        return jCiteVersion;
    }

    /**
     * ForgeRock link tester plugin version to use.
     *
     * @return {@link #linkTesterVersion}
     */
    public String getLinkTesterVersion() {
        return linkTesterVersion;
    }

    /**
     * Whether the ForgeRock link tester plugin should skip checking that
     * external URLs are valid. See the {@code skipUrls} parameter of the <a
     * href="https://github.com/aldaris/docbook-linktester/">link tester
     * plugin</a>.
     *
     * @return {@link #skipLinkCheck}
     */
    public String skipLinkCheck() {
        return skipLinkCheck;
    }

    /**
     * Whether to run the ForgeRock link tester plugin. You only need to run the
     * link tester plugin from the top level of a project, not the modules.
     *
     * @return {@link #runLinkTester}
     */
    public String runLinkTester() {
        return runLinkTester;
    }

    /**
     * Short name of the project, such as OpenAM, OpenDJ, OpenIDM.
     *
     * @return {@link #projectName}
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Google Analytics identifier for the project.
     *
     * @return {@link #googleAnalyticsId}
     */
    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    /**
     * Do not process these formats. Choices include: epub, html, man, pdf, rtf.
     * Do not set both excludes and includes in the same configuration.
     *
     * @return {@link #excludes}
     */
    public List<String> getExcludes() {
        return excludes;
    }

    /**
     * Process only this format. Choices include: epub, html, man, pdf, rtf.
     * Do not set both excludes and includes in the same configuration.
     *
     * @return {@link #include}
     */
    public String getInclude() {
        return include;
    }

    /**
     * Base directory for DocBook XML source files.
     *
     * @return {@link #docbkxSourceDirectory}
     */
    public File getDocbkxSourceDirectory() {
        return docbkxSourceDirectory;
    }

    /**
     * Base directory for filtered DocBook XML source files, relative to the
     * build directory.
     *
     * @return {@link #filteredDocbkxSourceDirectory}
     */
    public File getFilteredDocbkxSourceDirectory() {
        return new File(buildDirectory, filteredDocbkxSourceDirectory);
    }

    /**
     * Whether to use filtered sources.
     *
     * @return {@link #useFilteredSources}
     */
    public boolean doUseFilteredSources() {
        return useFilteredSources;
    }

    /**
     * Base directory for processed DocBook XML source files, relative to the
     * build directory.
     *
     * @return {@link #docbkxGeneratedSourceDirectory}
     */
    public File getDocbkxGeneratedSourceDirectory() {
        return new File(buildDirectory, docbkxGeneratedSourceDirectory);
    }

    /**
     * Whether to use generated sources.
     *
     * @return {@link #useGeneratedSources}
     */
    public boolean doUseGeneratedSources() {
        return useGeneratedSources;
    }

    /**
     * Base directory for built documentation, relative to the build directory.
     *
     * @return {@link #docbkxOutputDirectory}
     */
    public File getDocbkxOutputDirectory() {
        return new File(buildDirectory, docbkxOutputDirectory);
    }

    /**
     * Build directory for this plugin, usually {@code ${project.build.directory}.
     *
     * @return {@link #buildDirectory}
     */
    public File getBuildDirectory() {
        return buildDirectory;
    }

    /**
     * The Maven Project Object.
     *
     * @return {@link #project}
     */
    public MavenProject getProject() {
        return project;
    }

    /**
     * The Maven Session Object.
     *
     * @return {@link #session}
     */
    public MavenSession getSession() {
        return session;
    }

    /**
     * The Maven PluginManager Object.
     *
     * @return {@link #pluginManager}
     */
    public BuildPluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Top-level DocBook documents included in the documentation set such as
     * books, articles, and references share a common entry point, which is a
     * file having the name specified by this element.
     * <p/>
     * For example, if your documentation set has Release Notes, an Installation
     * Guide, a Developer's Guide, and a Reference, your source layout under the
     * base DocBook XML source directory might look like this, assuming you use
     * the default file name, <code>index.xml</code>.
     * <p/>
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
     * <p/>
     * The <code>...other files...</code> can have whatever names you want, as
     * long as the name does not conflict with the file name you set here.
     *
     * @return {@link #documentSrcName}
     */
    public String getDocumentSrcName() {
        return documentSrcName;
    }

    /**
     * Return a list of output formats to generate. If no defaults are
     * specified, then the default list of formats includes epub, html, man,
     * pdf, rtf.
     *
     * @param defaults (Restricted) list of formats to consider. Set this to limit
     *                 the list of output formats. Formats are passed on to the
     *                 plugin as is.
     * @return List of output formats.
     */
    public List<String> getOutputFormats(final String... defaults)
            throws MojoExecutionException {
        ArrayList<String> formats = new ArrayList<String>();

        if (defaults.length != 0) {                      // Restrict list.
            formats.addAll(Arrays.asList(defaults));
        } else {
            formats.addAll(Arrays.asList("epub", "html", "man", "pdf", "rtf"));
        }

        ArrayList<String> excludes = new ArrayList<String>();
        String include = "";

        if (getExcludes() != null) {
            excludes = (ArrayList<String>) getExcludes();
        }

        if (getInclude() != null) {
            include = getInclude();
        }

        if (!excludes.isEmpty() && !include.equals("")) {
            throw new MojoExecutionException("Do not set both <excludes> and "
                    + "<include> in the same configuration.");

        } else if (!excludes.isEmpty()) {          // Exclude formats.
            for (String format : excludes) {
                formats.remove(format);
            }
        } else if (formats.contains(getInclude())) {    // Include one format.
            formats.clear();
            formats.add(include);
        }
        return formats;
    }

    /**
     * Whether to use common legal notice and front matter to build docs.
     * Use of common content relies on {@code useGeneratedSources} being
     * {@code true} as the common content overwrites files of the same
     * name in the project.
     *
     * @parameter default-value="true" property="useSharedContent"
     */
    public boolean useSharedContent() {
        return useSharedContent;
    }
}
