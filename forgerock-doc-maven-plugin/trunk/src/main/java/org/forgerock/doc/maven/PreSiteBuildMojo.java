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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor;

/**
 * Implementation to build core documentation from <a
 * href="http://www.docbook.org/tdg51/en/html/docbook.html">DocBook XML</a>
 * using <a href="http://code.google.com/p/docbkx-tools/">docbkx-tools</a>. This
 * Mojo builds various output formats, but does not lay the documentation out in
 * a site format, as layout is performed in the site phase to allow for other
 * pre-site work, such as building Javadoc and other reference content.
 *
 * @Checkstyle:ignoreFor 2
 * @goal build
 * @phase pre-site
 */
public class PreSiteBuildMojo extends AbstractBuildMojo {

    // XML sources might be preprocessed, and image sources might not be.
    private File xmlSourceDirectory;
    private File imageSourceDirectory;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException {
        // TODO: Get these directly from the plugin .jar rather than copying.
        copyResources();

        // If sources are generated, for example for JCite, build documentation
        // from the generated sources, rather than the original sources.
        if (doUseGeneratedSources()) {
            xmlSourceDirectory = getDocbkxGeneratedSourceDirectory();
            imageSourceDirectory = getDocbkxSourceDirectory();
        } else {
            xmlSourceDirectory = getDocbkxSourceDirectory();
            imageSourceDirectory = getDocbkxSourceDirectory();
        }

        // The Executor is what actually calls other plugins.
        Executor exec = new Executor();

        List<String> formats = getOutputFormats();

        // Prepare FOP for printable output, e.g. PDF.
        if (formats.contains("pdf") || formats.contains("rtf")) {
            getLog().info("Preparing Apache FOP...");
            exec.prepareFOP();
        }

        // Get the common configuration for all output generation.
        getLog().info("Preparing common configuration...");
        ArrayList<MojoExecutor.Element> baseConf = exec.getBaseConfiguration();

        // Build and prepare EPUB for publishing.
        if (formats.contains("epub")) {
            getLog().info("Building EPUB...");
            exec.buildEPUB(baseConf);
            postProcessEPUB(getDocbkxOutputDirectory().getPath()
                    + File.separator + "epub");
        }

        // Build and prepare PDF for publishing.
        if (formats.contains("pdf")) {
            getLog().info("Building PDF...");
            exec.buildPDF(baseConf);
            postProcessPDF(getDocbkxOutputDirectory().getPath()
                    + File.separator + "pdf");
        }

        // Build and prepare RTF for publishing.
        if (formats.contains("rtf")) {
            getLog().info("Building RTF...");
            exec.buildRTF(baseConf);
            postProcessRTF(getDocbkxOutputDirectory().getPath()
                    + File.separator + "rtf");
        }

        // Build and prepare man pages for publishing.
        if (formats.contains("man")) {
            getLog().info("Building man pages...");
            exec.buildManpages(baseConf);
        }

        // Build and prepare HTML for publishing.
        if (formats.contains("html")) {
            getLog().info("Building single page HTML...");
            exec.buildSingleHTMLOlinkDB(baseConf);
            exec.buildSingleHTML(baseConf);

            getLog().info("Building chunked HTML...");
            exec.buildChunkedHTMLOlinkDB(baseConf);
            exec.buildChunkedHTML(baseConf);
            postProcessHTML(getDocbkxOutputDirectory().getPath()
                    + File.separator + "html");
        }
    }

    /**
     * Copy resources needed from plugin to project build directory. Resources
     * include custom fonts and XSL customization files.
     *
     * @throws MojoExecutionException Copy failed
     */
    final void copyResources() throws MojoExecutionException {
        // If you update this method, also see getBaseConfiguration().
        String[] resources = {"/fonts/DejaVuSans-Oblique.ttf",
            "/fonts/DejaVuSans.ttf", "/fonts/DejaVuSansCondensed-Bold.ttf",
            "/fonts/DejaVuSansCondensed-BoldOblique.ttf",
            "/fonts/DejaVuSansMono-Bold.ttf",
            "/fonts/DejaVuSansMono-BoldOblique.ttf",
            "/fonts/DejaVuSansMono-Oblique.ttf",
            "/fonts/DejaVuSansMono.ttf", "/fonts/DejaVuSerif-Italic.ttf",
            "/fonts/DejaVuSerif.ttf",
            "/fonts/DejaVuSerifCondensed-Bold.ttf",
            "/fonts/DejaVuSerifCondensed-BoldItalic.ttf",
            "/docbkx-stylesheets/epub/coredoc.xsl",
            "/docbkx-stylesheets/fo/coredoc.xsl",
            "/docbkx-stylesheets/fo/titlepages.xsl",
            "/docbkx-stylesheets/html/chunked.xsl",
            "/docbkx-stylesheets/html/coredoc.xsl",
            "/docbkx-stylesheets/man/coredoc.xsl"};

        for (String resource : resources) {
            URL src = getClass().getResource(resource);
            File dest = new File(getBuildDirectory()
                    + resource.replaceAll("/", File.separator));
            try {
                FileUtils.copyURLToFile(src, dest);
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to copy file: "
                        + resource + "\n" + e.getMessage());
            }
        }
    }

    /**
     * EPUB XSL stylesheet customization file.
     * <p/>
     * docbkx-tools element: &lt;epubCustomization&gt;
     *
     * @parameter default-value=
     * "${project.build.directory}/docbkx-stylesheets/epub/coredoc.xsl"
     * @required
     */
    private File epubCustomization;

    /**
     * Prepare built EPUB documents for publication. Currently this method
     * renames the files.
     *
     * @param epubDir Directory under which to find the built files
     * @throws MojoExecutionException Something went wrong updating files.
     */
    final void postProcessEPUB(final String epubDir) throws MojoExecutionException {
        renameDocuments(epubDir, "epub");
    }

    /**
     * Rename built documents named ${getDocumentSrcName()} + extension. For
     * example, rename <code>admin-guide/index.epub</code> to
     * <code>admin-guide/OpenAM-Admin-Guide.epub</code>.
     *
     * @param base Directory under which to find the built documents
     * @param ext  File name extension including the dot, e.g. .pdf
     * @throws MojoExecutionException Something went wrong renaming files.
     */
    final void renameDocuments(final String base, final String ext) throws MojoExecutionException {
        String s = File.separator;
        String baseName = FilenameUtils.getBaseName(getDocumentSrcName());

        Set<String> docNames = DocUtils.getDocumentNames(
                xmlSourceDirectory, getDocumentSrcName());
        if (docNames.isEmpty()) {
            throw new MojoExecutionException("No document names found.");
        }

        for (String docName : docNames) {
            String newName = DocUtils.renameDoc(getProjectName(), docName, ext);
            File src = new File(base + s + docName + s + baseName + "." + ext);
            File dest = new File(base + s + docName + s + newName);
            if (newName.equals("") || !src.renameTo(dest)) {
                throw new MojoExecutionException("Failed to rename "
                        + src.getPath() + " to " + dest.getPath());
            }
        }
    }

    /**
     * Rename a single built document ${getDocumentSrcName()} + extension. For
     * example, rename <code>index.pdf</code> to
     * <code>OpenAM-Admin-Guide.pdf</code>.
     *
     * @param file    File to rename, such as <code>index.pdf</code>
     * @param docName Simple document name such as <code>admin-guide</code>
     * @throws MojoExecutionException Something went wrong renaming the file.
     */
    final void renameDocument(final File file, final String docName) throws MojoExecutionException {

        String ext = FilenameUtils.getExtension(file.getName());
        String newName = file.getParent() + File.separator
                + DocUtils.renameDoc(getProjectName(), docName, ext);
        try {
            File newFile = new File(newName);
            if (!newFile.exists()) {
                FileUtils.moveFile(file, newFile);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to rename " + file);
        }
    }

    /**
     * FO XSL stylesheet customization file (for PDF, RTF).
     * <p/>
     * docbkx-tools element: &lt;foCustomization&gt;
     *
     * @parameter default-value=
     * "${project.build.directory}/docbkx-stylesheets/fo/coredoc.xsl"
     * @required
     */
    private File foCustomization;

    /**
     * Prepare built PDF documents for publication. Currently this method does
     * not do anything.
     *
     * @param pdfDir Directory under which to find the built files
     * @throws MojoExecutionException Something went wrong updating files.
     */
    final void postProcessPDF(final String pdfDir) throws MojoExecutionException {
    }

    /**
     * Prepare built RTF documents for publication. Currently this method does
     * not do anything.
     *
     * @param rtfDir Directory under which to find the built files
     * @throws MojoExecutionException Something went wrong updating files.
     */
    final void postProcessRTF(final String rtfDir) throws MojoExecutionException {
    }

    /**
     * Man page XSL stylesheet customization file.
     * <p/>
     * docbkx-tools element: &lt;manpagesCustomization&gt;
     *
     * @parameter default-value=
     * "${project.build.directory}/docbkx-stylesheets/man/coredoc.xsl"
     * @required
     */
    private File manpagesCustomization;

    /**
     * Single page HTML XSL stylesheet customization file.
     * <p/>
     * docbkx-tools element: &lt;htmlCustomization&gt;
     *
     * @parameter default-value=
     * "${project.build.directory}/docbkx-stylesheets/html/coredoc.xsl"
     * @required
     */
    private File singleHTMLCustomization;

    /**
     * Get absolute path to a temporary Olink target database XML document that
     * points to the individual generated Olink DB files, for single page HTML.
     *
     * @return Absolute path to the temporary file
     * @throws MojoExecutionException Could not write target DB file.
     */
    final String buildSingleHTMLTargetDB() throws MojoExecutionException {
        File targetDB = new File(getBuildDirectory() + File.separator
                + "olinkdb-single-page-html.xml");
        try {
            StringBuilder content = new StringBuilder();
            content.append("<?xml version='1.0' encoding='utf-8'?>\n")
                    .append("<!DOCTYPE targetset[\n");

            String targetDbDtd = IOUtils.toString(getClass()
                    .getResourceAsStream("/targetdatabase.dtd"));
            content.append(targetDbDtd).append("\n");

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            for (String docName : docNames) {
                String sysId = getBuildDirectory().getAbsolutePath()
                        + File.separator + docName + "-single.target.db";
                content.append("<!ENTITY ").append(docName)
                        .append(" SYSTEM '").append(sysId).append("'>\n");
            }

            content.append("]>\n")

                    .append("<targetset>\n")
                    .append(" <targetsetinfo>Target DB for ForgeRock DocBook content,\n")
                    .append(" for use with single page HTML only.</targetsetinfo>\n")
                    .append(" <sitemap>\n")
                    .append("  <dir name='doc'>\n");

            for (String docName : docNames) {
                content.append("   <document targetdoc='").append(docName).append("'\n")
                        .append("             baseuri='../").append(docName)
                        .append("/").append(FilenameUtils.getBaseName(getDocumentSrcName()))
                        .append(".html'>\n")
                        .append("    &").append(docName).append(";\n")
                        .append("   </document>\n");
            }
            content.append("  </dir>\n")
                    .append(" </sitemap>\n")
                    .append("</targetset>\n");

            FileUtils.writeStringToFile(targetDB, content.toString());
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to write Olink target database: " + e.getMessage());
        }
        return targetDB.getPath();
    }

    /**
     * Chunked HTML XSL stylesheet customization file.
     * <p/>
     * docbkx-tools element: &lt;htmlCustomization&gt;
     *
     * @parameter default-value=
     * "${project.build.directory}/docbkx-stylesheets/html/chunked.xsl"
     * @required
     */
    private File chunkedHTMLCustomization;

    /**
     * Get absolute path to a temporary Olink target database XML document that
     * points to the individual generated Olink DB files, for chunked HTML.
     *
     * @return Absolute path to the temporary file
     * @throws MojoExecutionException Could not write target DB file.
     */
    final String buildChunkedHTMLTargetDB() throws MojoExecutionException {
        File targetDB = new File(getBuildDirectory() + File.separator
                + "olinkdb-chunked-html.xml");
        try {
            StringBuilder content = new StringBuilder();
            content.append("<?xml version='1.0' encoding='utf-8'?>\n")
                    .append("<!DOCTYPE targetset[\n");

            String targetDbDtd = IOUtils.toString(getClass()
                    .getResourceAsStream("/targetdatabase.dtd"));
            content.append(targetDbDtd).append("\n");

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            for (String docName : docNames) {
                String sysId = getBuildDirectory().getAbsolutePath()
                        + File.separator + docName + "-chunked.target.db";
                content.append("<!ENTITY ").append(docName)
                        .append(" SYSTEM '").append(sysId).append("'>\n");
            }

            content.append("]>\n")

                    .append("<targetset>\n")
                    .append(" <targetsetinfo>Target DB for ForgeRock DocBook content,\n")
                    .append(" for use with chunked HTML only.</targetsetinfo>\n")
                    .append(" <sitemap>\n")
                    .append("  <dir name='doc'>\n");

            String baseName = FilenameUtils.getBaseName(getDocumentSrcName());
            for (String docName : docNames) {
                content.append("   <dir name='").append(docName).append("'>\n")
                        .append("    <dir name='").append(baseName).append("'>\n")
                        .append("     <document targetdoc='").append(docName).append("'\n")
                        .append("               baseuri='../../")
                        .append(docName).append("/").append(baseName).append("/'>\n")
                        .append("      &").append(docName).append(";\n")
                        .append("     </document>\n")
                        .append("    </dir>\n")
                        .append("   </dir>\n");
            }

            content.append("  </dir>\n")
                    .append(" </sitemap>\n")
                    .append("</targetset>\n");

            FileUtils.writeStringToFile(targetDB, content.toString());
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to write Olink target database: " + e.getMessage());
        }
        return targetDB.getPath();
    }

    /**
     * Prepare single and chunked HTML for publication.
     * <p/>
     * The HTML built by docbkx-tools does not currently include the following,
     * which this method adds.
     * <ul>
     * <li>A DOCTYPE declaration (needed to get Internet Explorer to interpret
     * CSS correctly</li>
     * <li>JavaScript to workaround a long-standing Firefox issue, and the
     * fold/unfold long lines in Screen elements</li>
     * <li>A favicon link</li>
     * <li>JavaScript used by Google Analytics</li>
     * <li>CSS to style the HTML</li>
     * </ul>
     *
     * @param htmlDir Directory under which to find HTML output
     * @throws MojoExecutionException Something went wrong when updating HTML.
     */
    final void postProcessHTML(final String htmlDir) throws MojoExecutionException {
        try {
            getLog().info("Editing built HTML...");
            HashMap<String, String> replacements = new HashMap<String, String>();

            String doctype = IOUtils.toString(
                    getClass().getResourceAsStream("/starthtml-doctype.txt"),
                    "UTF-8");
            replacements.put("<html>", doctype);

            String javascript = IOUtils.toString(getClass()
                    .getResourceAsStream("/endhead-js-favicon.txt"), "UTF-8");
            replacements.put("</head>", javascript);

            String linkToJira = getLinkToJira();
            String gascript = IOUtils.toString(
                    getClass().getResourceAsStream("/endbody-ga.txt"), "UTF-8");
            gascript = gascript.replace("ANALYTICS-ID", getGoogleAnalyticsId());
            replacements.put("</body>", linkToJira + "\n" + gascript);

            HTMLUtils.updateHTML(htmlDir, replacements);

            getLog().info("Adding CSS...");
            File css = new File(getBuildDirectory().getPath() + File.separator
                    + "coredoc.css");
            FileUtils.deleteQuietly(css);
            FileUtils
                    .copyURLToFile(getClass().getResource("/coredoc.css"), css);
            HTMLUtils.addCss(htmlDir, css,
                    FilenameUtils.getBaseName(getDocumentSrcName()) + ".html");
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to update output HTML correctly: " + e.getMessage());
        }
    }

    /**
     * Return a &lt;p&gt; containing a link to log a bug in Jira. The string
     * is not localized.
     *
     * @return &lt;p&gt; containing a link to log a bug in Jira
     */
    final String getLinkToJira() {
        String link = "<p>&nbsp;</p><div id=\"footer\"><p>Something wrong on this page? "
                + "<a href=\"JIRA-URL\">Log a documentation bug.</a></p></div>";

        // https://confluence.atlassian.com/display/JIRA/Creating+Issues+via+direct+HTML+links
        String jiraURL = "";
        if (getProjectName().equalsIgnoreCase("OpenAM")) {
            jiraURL = "https://bugster.forgerock.org/jira/secure/CreateIssueDetails!init.jspa?"
                    + "pid=10000&components=10007&issuetype=1";
        }
        if (getProjectName().equalsIgnoreCase("OpenDJ")) {
            jiraURL = "https://bugster.forgerock.org/jira/secure/CreateIssueDetails!init.jspa?"
                    + "pid=10040&components=10132&issuetype=1";
        }
        if (getProjectName().equalsIgnoreCase("OpenICF")) {
            jiraURL = "https://bugster.forgerock.org/jira/secure/CreateIssueDetails!init.jspa?"
                    + "pid=10041&components=10170&issuetype=1";
        }
        if (getProjectName().equalsIgnoreCase("OpenIDM")) {
            jiraURL = "https://bugster.forgerock.org/jira/secure/CreateIssueDetails!init.jspa?"
                    + "pid=10020&components=10164&issuetype=1";
        }
        if (getProjectName().equalsIgnoreCase("OpenIG")) {
            jiraURL = "https://bugster.forgerock.org/jira/secure/CreateIssueDetails!init.jspa?"
                    + "pid=10060&components=10220&issuetype=1";
        }
        if (getProjectName().equalsIgnoreCase("ForgeRock")) { // Just testing
            jiraURL = "https://bugster.forgerock.org/jira/secure/CreateIssueDetails!init.jspa?"
                    + "pid=10010&issuetype=1";
        }

        if (jiraURL.equals("")) {
            link = "";
        } else {
            link = link.replaceFirst("JIRA-URL", jiraURL);
        }
        return link;
    }

    /**
     * Directory where fonts and font metrics are stored.
     *
     * @parameter default-value="${project.build.directory}/fonts"
     * property="fontsDirectory"
     * @required
     */
    private File fontsDirectory;

    /**
     * Should WinAnsi encoding be used for embedded fonts?
     *
     * @parameter default-value="false"
     * @required
     */
    private String ansi;

    /**
     * Are these draft documents, rather than final documents?
     * <p/>
     * docbkx-tools element: &lt;draftMode&gt;
     *
     * @parameter default-value="yes" property="isDraftMode"
     * @required
     */
    private String isDraftMode;

    /**
     * For draft mode, URL to the background watermark image.
     * <p/>
     * docbkx-tools element: &lt;draftWatermarkImage&gt;
     *
     * @parameter default-value=
     * "http://docbook.sourceforge.net/release/images/draft.png"
     * property="draftWatermarkURL"
     * @required
     */
    private String draftWatermarkURL;

    /**
     * Should &lt;programlisting&gt; content have syntax highlighting?
     * <p/>
     * docbkx-tools element: &lt;highlightSource&gt;
     *
     * @parameter default-value="1" property="useSyntaxHighlighting"
     * @required
     */
    private String useSyntaxHighlighting;

    /**
     * Should sections have numeric labels?
     * <p/>
     * docbkx-tools element: &lt;sectionAutolabel&gt;
     *
     * @parameter default-value="true" property="areSectionsAutolabeled"
     * @required
     */
    private String areSectionsAutolabeled;

    /**
     * Should section labels include parent numbers, like 1.1, 1.2, 1.2.1,
     * 1.2.2?
     * <p/>
     * docbkx-tools element: &lt;sectionLabelIncludesComponentLabel&gt;
     *
     * @parameter default-value="true"
     * property="doesSectionLabelIncludeComponentLabel"
     * @required
     */
    private String doesSectionLabelIncludeComponentLabel;

    /**
     * Should documents be allowed to include other documents?
     * <p/>
     * docbkx-tools element: &lt;xincludeSupported&gt;
     *
     * @parameter default-value="true" property="isXincludeSupported"
     * @required
     */
    private String isXincludeSupported;

    /**
     * See return.
     *
     * @return {@link #epubCustomization}
     */
    public final File getEpubCustomization() {
        return epubCustomization;
    }

    /**
     * See return.
     *
     * @return {@link #foCustomization}
     */
    public final File getFoCustomization() {
        return foCustomization;
    }

    /**
     * See return.
     *
     * @return {@link #manpagesCustomization}
     */
    public final File getManpagesCustomization() {
        return manpagesCustomization;
    }

    /**
     * See return.
     *
     * @return {@link #singleHTMLCustomization}
     */
    public final File getSingleHTMLCustomization() {
        return singleHTMLCustomization;
    }

    /**
     * See return.
     *
     * @return {@link #chunkedHTMLCustomization}
     */
    public final File getChunkedHTMLCustomization() {
        return chunkedHTMLCustomization;
    }

    /**
     * See return.
     *
     * @return {@link #fontsDirectory}
     */
    public final File getFontsDirectory() {
        return fontsDirectory;
    }

    /**
     * See return.
     *
     * @return {@link #ansi}
     */
    public final String getAnsi() {
        return ansi;
    }

    /**
     * See return.
     *
     * @return {@link #isDraftMode}
     */
    public final String getIsDraftMode() {
        return isDraftMode;
    }

    /**
     * See return.
     *
     * @return {@link #draftWatermarkURL}
     */
    public final String getDraftWatermarkURL() {
        return draftWatermarkURL;
    }

    /**
     * See return.
     *
     * @return {@link #useSyntaxHighlighting}
     */
    public final String getUseSyntaxHighlighting() {
        return useSyntaxHighlighting;
    }

    /**
     * See return.
     *
     * @return {@link #areSectionsAutolabeled}
     */
    public final String getAreSectionsAutolabeled() {
        return areSectionsAutolabeled;
    }

    /**
     * See return.
     *
     * @return {@link #doesSectionLabelIncludeComponentLabel}
     */
    public final String getDoesSectionLabelIncludeComponentLabel() {
        return doesSectionLabelIncludeComponentLabel;
    }

    /**
     * See return.
     *
     * @return {@link #isXincludeSupported}
     */
    public final String getIsXincludeSupported() {
        return isXincludeSupported;
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {
        /**
         * Prepare Apache FOP for output formats like PDF. This step involves
         * font metrics generation.
         *
         * @throws MojoExecutionException Failed to prepare FOP.
         */
        void prepareFOP() throws MojoExecutionException {
            String fontsDir = FilenameUtils.separatorsToUnix(fontsDirectory
                    .getPath());
            executeMojo(
                    plugin(groupId("com.agilejava.docbkx"),
                            artifactId("docbkx-fop-support"),
                            version(getDocbkxVersion())),
                    goal("generate"),
                    configuration(element(name("ansi"), ansi),
                            element(name("sourceDirectory"), fontsDir),
                            element(name("targetDirectory"), fontsDir)),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }

        /**
         * Returns element array for common configuration of all executions of
         * the docbkx-maven-plugin.
         *
         * @return Configuration applicable to all executions
         */
        ArrayList<MojoExecutor.Element> getBaseConfiguration() {
            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();

            cfg.add(element(name("draftMode"), isDraftMode));
            cfg.add(element(name("draftWatermarkImage"), draftWatermarkURL));
            cfg.add(element(name("highlightSource"), useSyntaxHighlighting));
            cfg.add(element(name("sectionAutolabel"), areSectionsAutolabeled));
            cfg.add(element(name("sectionLabelIncludesComponentLabel"),
                    doesSectionLabelIncludeComponentLabel));
            cfg.add(element(name("xincludeSupported"), isXincludeSupported));
            cfg.add(element(name("sourceDirectory"), FilenameUtils
                    .separatorsToUnix(xmlSourceDirectory.getPath())));

            return cfg;
        }

        /**
         * Build EPUB documents from DocBook XML sources.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to build the output.
         */
        void buildEPUB(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {

            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.addAll(baseConfiguration);
            cfg.add(element(name("includes"), "*/" + getDocumentSrcName()));
            cfg.add(element(name("epubCustomization"), FilenameUtils
                    .separatorsToUnix(getEpubCustomization().getPath())));

            // Copy images from source to build. DocBook XSL does not copy the
            // images, because XSL does not have a facility for copying files.
            // Unfortunately, neither does docbkx-tools.

            String baseName = FilenameUtils.getBaseName(getDocumentSrcName());

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            for (String docName : docNames) {
                File srcDir = new File(imageSourceDirectory, docName
                        + File.separator + "images");
                File destDir = new File(getDocbkxOutputDirectory(), "epub"
                        + File.separator + docName + File.separator + baseName
                        + File.separator + "images");
                try {
                    if (srcDir.exists()) {
                        FileUtils.copyDirectory(srcDir, destDir);
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(
                            "Failed to copy images from " + srcDir + " to "
                                    + destDir);
                }
            }

            executeMojo(
                    plugin(groupId("com.agilejava.docbkx"),
                            artifactId("docbkx-maven-plugin"),
                            version(getDocbkxVersion())),
                    goal("generate-epub"),
                    configuration(cfg.toArray(new Element[0])),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }

        /**
         * Build FO documents from DocBook XML sources, including fonts.
         *
         * @param baseConfiguration Common configuration for all executions
         * @param format            Specific output format (pdf, rtf)
         * @throws MojoExecutionException Failed to build the output.
         */
        void buildFO(final ArrayList<MojoExecutor.Element> baseConfiguration,
                     final String format) throws MojoExecutionException {
            if (!(format.equalsIgnoreCase("pdf") || format
                    .equalsIgnoreCase("rtf"))) {
                throw new MojoExecutionException("Output format " + format
                        + " is not supported." + " Use either pdf or rtf.");
            }

            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.addAll(baseConfiguration);
            cfg.add(element(name("foCustomization"),
                    FilenameUtils.separatorsToUnix(foCustomization.getPath())));

            // If you update this list, also see copyFonts().
            String fontDir = FilenameUtils.separatorsToUnix(fontsDirectory
                    .getPath());
            cfg.add(element(
                    name("fonts"),
                    element(name("font"),
                            element(name("name"), "DejaVuSans"),
                            element(name("style"), "normal"),
                            element(name("weight"), "normal"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSans.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSans-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSans"),
                            element(name("style"), "normal"),
                            element(name("weight"), "bold"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSansCondensed-Bold.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSansCondensed-Bold-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSans"),
                            element(name("style"), "italic"),
                            element(name("weight"), "normal"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSans-Oblique.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSans-Oblique-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSans"),
                            element(name("style"), "italic"),
                            element(name("weight"), "bold"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSansCondensed-BoldOblique.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSansCondensed-BoldOblique-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSansMono"),
                            element(name("style"), "normal"),
                            element(name("weight"), "normal"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSansMono.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSansMono-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSansMono"),
                            element(name("style"), "normal"),
                            element(name("weight"), "bold"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSansMono-Bold.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSansMono-Bold-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSansMono"),
                            element(name("style"), "italic"),
                            element(name("weight"), "normal"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSansMono-Oblique.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSansMono-Oblique-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSansMono"),
                            element(name("style"), "italic"),
                            element(name("weight"), "bold"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSansMono-BoldOblique.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSansMono-BoldOblique-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSerif"),
                            element(name("style"), "normal"),
                            element(name("weight"), "normal"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSerif.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSerif-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSerif"),
                            element(name("style"), "normal"),
                            element(name("weight"), "bold"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSerifCondensed-Bold.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSerifCondensed-Bold-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSerif"),
                            element(name("style"), "italic"),
                            element(name("weight"), "normal"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSerif-Italic.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSerif-Italic-metrics.xml")),
                    element(name("font"),
                            element(name("name"), "DejaVuSerif"),
                            element(name("style"), "italic"),
                            element(name("weight"), "bold"),
                            element(name("embedFile"), fontDir
                                    + "/DejaVuSerifCondensed-BoldItalic.ttf"),
                            element(name("metricsFile"), fontDir
                                    + "/DejaVuSerifCondensed-BoldItalic-metrics.xml"))));

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            // When using generated sources, copy the images manually.
            if (xmlSourceDirectory != getDocbkxSourceDirectory()) {
                for (String docName : docNames) {
                    File srcDir = new File(imageSourceDirectory, docName
                            + File.separator + "images");
                    File destDir = new File(xmlSourceDirectory, docName
                            + File.separator + "images");
                    try {
                        if (srcDir.exists()) {
                            FileUtils.copyDirectory(srcDir, destDir);
                        }
                    } catch (IOException e) {
                        throw new MojoExecutionException(
                                "Failed to copy images from " + srcDir + " to "
                                        + destDir);
                    }
                }
            }

            for (String docName : docNames) {
                cfg.add(element(name("includes"), docName + "/"
                        + getDocumentSrcName()));

                // Permit hyphenation.
                Dependency offo = new Dependency();
                offo.setGroupId("net.sf.offo");
                offo.setArtifactId("fop-hyph");
                offo.setVersion("1.2");
                offo.setScope("runtime");
                Plugin plugin = plugin(groupId("com.agilejava.docbkx"),
                        artifactId("docbkx-maven-plugin"),
                        version(getDocbkxVersion()));
                plugin.addDependency(offo);

                executeMojo(
                        plugin,
                        goal("generate-" + format),
                        configuration(cfg.toArray(new Element[0])),
                        executionEnvironment(getProject(), getSession(),
                                getPluginManager()));

                // Avoid each new document overwriting the last.
                File file = new File(getDocbkxOutputDirectory(), format
                        + File.separator
                        + FilenameUtils.getBaseName(getDocumentSrcName()) + "."
                        + format);
                renameDocument(file, docName);
            }
        }

        /**
         * Build PDF documents from DocBook XML sources.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to build the output.
         */
        void buildPDF(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {
            buildFO(baseConfiguration, "pdf");
        }

        /**
         * Build RTF documents from DocBook XML sources.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to build the output.
         */
        void buildRTF(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {
            buildFO(baseConfiguration, "rtf");
        }

        /**
         * Build reference manual pages from DocBook XML sources.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to build the output.
         */
        void buildManpages(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {
            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.addAll(baseConfiguration);
            cfg.add(element(name("includes"), "*/" + getDocumentSrcName()));
            cfg.add(element(name("manpagesCustomization"), FilenameUtils
                    .separatorsToUnix(manpagesCustomization.getPath())));

            executeMojo(
                    plugin(groupId("com.agilejava.docbkx"),
                            artifactId("docbkx-maven-plugin"),
                            version(getDocbkxVersion())),
                    goal("generate-manpages"),
                    configuration(cfg.toArray(new Element[0])),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }

        /**
         * Prepare Olink database files for single page HTML output.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to prepare the target DB files.
         */
        void buildSingleHTMLOlinkDB(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {
            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.add(element(name("xincludeSupported"), isXincludeSupported));
            cfg.add(element(name("sourceDirectory"), FilenameUtils
                    .separatorsToUnix(xmlSourceDirectory.getPath())));

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            for (String docName : docNames) {
                cfg.add(element(name("includes"), docName + "/"
                        + getDocumentSrcName()));
                cfg.add(element(name("collectXrefTargets"), "only"));
                cfg.add(element(
                        name("targetsFilename"),
                        FilenameUtils.separatorsToUnix(getBuildDirectory()
                                .getPath())
                                + "/"
                                + docName
                                + "-single.target.db"));

                executeMojo(
                        plugin(groupId("com.agilejava.docbkx"),
                                artifactId("docbkx-maven-plugin"),
                                version(getDocbkxVersion())),
                        goal("generate-html"),
                        configuration(cfg.toArray(new Element[0])),
                        executionEnvironment(getProject(), getSession(),
                                getPluginManager()));

                File outputDir = new File(getDocbkxOutputDirectory(), "html"
                        + File.separator + docName);
                try {
                    FileUtils.deleteDirectory(outputDir);
                } catch (IOException e) {
                    throw new MojoExecutionException("Cannot delete "
                            + outputDir);
                }
            }
        }

        /**
         * Build single page HTML from DocBook XML sources.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to build the output.
         */
        void buildSingleHTML(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {
            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.addAll(baseConfiguration);
            cfg.add(element(name("includes"), "*/" + getDocumentSrcName()));
            cfg.add(element(name("chunkedOutput"), "false"));
            cfg.add(element(name("htmlCustomization"), FilenameUtils
                    .separatorsToUnix(singleHTMLCustomization.getPath())));
            cfg.add(element(name("targetDatabaseDocument"),
                    buildSingleHTMLTargetDB()));

            // Copy images from source to build. DocBook XSL does not copy the
            // images, because XSL does not have a facility for copying files.
            // Unfortunately, neither does docbkx-tools.

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            for (String docName : docNames) {
                File srcDir = new File(imageSourceDirectory, docName
                        + File.separator + "images");
                File destDir = new File(getDocbkxOutputDirectory(), "html"
                        + File.separator + docName + File.separator + "images");
                try {
                    if (srcDir.exists()) {
                        FileUtils.copyDirectory(srcDir, destDir);
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(
                            "Failed to copy images from " + srcDir + " to "
                                    + destDir);
                }
            }

            executeMojo(
                    plugin(groupId("com.agilejava.docbkx"),
                            artifactId("docbkx-maven-plugin"),
                            version(getDocbkxVersion())),
                    goal("generate-html"),
                    configuration(cfg.toArray(new Element[0])),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }

        /**
         * Prepare Olink database files for chunked HTML output.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to prepare the target DB files.
         */
        void buildChunkedHTMLOlinkDB(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {
            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.add(element(name("xincludeSupported"), isXincludeSupported));
            cfg.add(element(name("sourceDirectory"), FilenameUtils
                    .separatorsToUnix(xmlSourceDirectory.getPath())));
            cfg.add(element(name("chunkedOutput"), "true"));
            cfg.add(element(name("htmlCustomization"), FilenameUtils
                    .separatorsToUnix(chunkedHTMLCustomization.getPath())));

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            for (String docName : docNames) {
                cfg.add(element(name("currentDocid"), docName));
                cfg.add(element(name("includes"), docName + "/"
                        + getDocumentSrcName()));
                cfg.add(element(name("collectXrefTargets"), "only"));
                cfg.add(element(
                        name("targetsFilename"),
                        FilenameUtils.separatorsToUnix(getBuildDirectory()
                                .getPath())
                                + "/"
                                + docName
                                + "-chunked.target.db"));

                executeMojo(
                        plugin(groupId("com.agilejava.docbkx"),
                                artifactId("docbkx-maven-plugin"),
                                version(getDocbkxVersion())),
                        goal("generate-html"),
                        configuration(cfg.toArray(new Element[0])),
                        executionEnvironment(getProject(), getSession(),
                                getPluginManager()));

                File outputDir = new File(getDocbkxOutputDirectory(), "html"
                        + File.separator + docName + File.separator
                        + FilenameUtils.getBaseName(getDocumentSrcName()));
                try {
                    FileUtils.deleteDirectory(outputDir);
                } catch (IOException e) {
                    throw new MojoExecutionException("Cannot delete "
                            + outputDir);
                }
            }
        }

        /**
         * Build chunked HTML pages from DocBook XML sources.
         *
         * @param baseConfiguration Common configuration for all executions
         * @throws MojoExecutionException Failed to build the output.
         */
        void buildChunkedHTML(final ArrayList<MojoExecutor.Element> baseConfiguration) throws
                MojoExecutionException {
            ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.addAll(baseConfiguration);
            cfg.add(element(name("includes"), "*/" + getDocumentSrcName()));
            cfg.add(element(name("chunkedOutput"), "true"));
            cfg.add(element(name("htmlCustomization"), FilenameUtils
                    .separatorsToUnix(chunkedHTMLCustomization.getPath())));
            cfg.add(element(name("targetDatabaseDocument"),
                    buildChunkedHTMLTargetDB()));

            // Copy images from source to build. DocBook XSL does not copy the
            // images, because XSL does not have a facility for copying files.
            // Unfortunately, neither does docbkx-tools.

            String baseName = FilenameUtils.getBaseName(getDocumentSrcName());

            Set<String> docNames = DocUtils.getDocumentNames(
                    xmlSourceDirectory, getDocumentSrcName());
            if (docNames.isEmpty()) {
                throw new MojoExecutionException("No document names found.");
            }

            for (String docName : docNames) {
                File srcDir = new File(imageSourceDirectory, docName
                        + File.separator + "images");
                File destDir = new File(getDocbkxOutputDirectory(), "html"
                        + File.separator + docName + File.separator + baseName
                        + File.separator + "images");
                try {
                    if (srcDir.exists()) {
                        FileUtils.copyDirectory(srcDir, destDir);
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(
                            "Failed to copy images from " + srcDir + " to "
                                    + destDir);
                }
            }

            executeMojo(
                    plugin(groupId("com.agilejava.docbkx"),
                            artifactId("docbkx-maven-plugin"),
                            version(getDocbkxVersion())),
                    goal("generate-html"),
                    configuration(cfg.toArray(new Element[0])),
                    executionEnvironment(getProject(), getSession(),
                            getPluginManager()));
        }
    }
}
