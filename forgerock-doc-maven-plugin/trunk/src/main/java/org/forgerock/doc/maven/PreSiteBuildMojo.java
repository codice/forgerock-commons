/*
 * MPL 2.0 HEADER START
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If applicable, add the following below this MPL 2.0 HEADER, replacing
 * the fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *     Portions Copyright [yyyy] [name of copyright owner]
 *
 * MPL 2.0 HEADER END
 *
 *     Copyright 2012 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;



import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
 * @goal build
 * @phase pre-site
 */
public class PreSiteBuildMojo extends AbstractBuildMojo
{

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute() throws MojoExecutionException
  {
    // TODO: Get these directly from the plugin .jar rather than copying.
    copyResources();

    Executor exec = new Executor();

    // Prepare FOP for printable output, e.g. PDF.
    getLog().info("Preparing Apache FOP...");
    exec.prepareFOP();

    // Get the common configuration for all output generation.
    getLog().info(
        "Preparing common configuration...");
    ArrayList<MojoExecutor.Element> baseConf = exec
        .getBaseConfiguration();

    // Prepare Olink database files for inter-document links declared in
    // the DocBook XML source. For background, try
    // http://www.sagehill.net/docbookxsl/Olinking.html
    getLog().info("Preparing Olink DB files...");
    exec.buildOlinkDB(baseConf);

    if (getExcludes() == null)
    {
      setExcludes(new ArrayList<String>());
    }

    // Build and prepare EPUB for publishing.
    if (getExcludes().isEmpty() || !getExcludes().contains("epub"))
    {
      getLog().info("Building EPUB...");
      exec.buildEPUB(baseConf);
      postProcessEPUB(docbkxOutputDirectory.getPath() + File.separator
          + "epub");
    }

    // Build and prepare PDF for publishing.
    if (getExcludes().isEmpty() || !getExcludes().contains("pdf"))
    {
      getLog().info("Building PDF...");
      exec.buildPDF(baseConf);
      postProcessPDF(docbkxOutputDirectory.getPath() + File.separator
          + "pdf");
    }

    // Build and prepare RTF for publishing.
    if (getExcludes().isEmpty() || !getExcludes().contains("rtf"))
    {
      getLog().info("Building RTF...");
      exec.buildRTF(baseConf);
      postProcessRTF(docbkxOutputDirectory.getPath() + File.separator
          + "rtf");
    }

    // Build and prepare man pages for publishing.
    if (getExcludes().isEmpty() || !getExcludes().contains("man"))
    {
      getLog().info("Building man pages...");
      exec.buildManpages(baseConf);
    }

    // Build and prepare HTML for publishing.
    if (getExcludes().isEmpty() || !getExcludes().contains("html"))
    {
      getLog().info("Building single HTML pages...");
      exec.buildSingleHTML(baseConf);
      getLog().info("Building chunked HTML pages...");
      exec.buildChunkedHTML(baseConf);
      postProcessHTML(docbkxOutputDirectory.getPath() + File.separator
          + "html");
    }
  }



  /**
   * Copy resources needed from plugin to project build directory.
   *
   * @throws MojoExecutionException
   *           Copy failed
   */
  void copyResources() throws MojoExecutionException
  {
    // If you update this method, also see getBaseConfiguration().
    String[] resources = {
        "/fonts/DejaVuSans-Oblique.ttf",
        "/fonts/DejaVuSans.ttf",
        "/fonts/DejaVuSansCondensed-Bold.ttf",
        "/fonts/DejaVuSansCondensed-BoldOblique.ttf",
        "/fonts/DejaVuSansMono-Bold.ttf",
        "/fonts/DejaVuSansMono-BoldOblique.ttf",
        "/fonts/DejaVuSansMono-Oblique.ttf",
        "/fonts/DejaVuSansMono.ttf",
        "/fonts/DejaVuSerif-Italic.ttf",
        "/fonts/DejaVuSerif.ttf",
        "/fonts/DejaVuSerifCondensed-Bold.ttf",
        "/fonts/DejaVuSerifCondensed-BoldItalic.ttf",
        "/docbkx-stylesheets/epub/coredoc.xsl",
        "/docbkx-stylesheets/fo/coredoc.xsl",
        "/docbkx-stylesheets/fo/titlepages.xsl",
        "/docbkx-stylesheets/html/chunked.xsl",
        "/docbkx-stylesheets/html/coredoc.xsl",
        "/docbkx-stylesheets/man/coredoc.xsl"
        };

    for (String resource : resources)
    {
      URL src = getClass().getResource(resource);
      File dest = new File(buildDirectory
          + resource.replaceAll("/", File.separator));
      try
      {
        FileUtils.copyURLToFile(src, dest);
      }
      catch (IOException e)
      {
        throw new MojoExecutionException("Failed to copy file: "
            + resource + "\n" + e.getMessage());
      }
    }
  }



  /**
   * EPUB XSL stylesheet customization file
   * <p>
   * docbkx-tools element: &lt;epubCustomization&gt;
   *
   * @parameter default-value=
   *            "${project.build.directory}/docbkx-stylesheets/epub/coredoc.xsl"
   * @required
   */
  protected File epubCustomization;



  /**
   * Prepare built EPUB documents for publication. Currently this method renames
   * the files.
   *
   * @param epubDir
   *          Directory under which to find the built files
   * @throws MojoExecutionException
   *           Something went wrong updating files.
   */
  void postProcessEPUB(String epubDir) throws MojoExecutionException
  {
    renameDocuments(epubDir, "epub");
  }



  /**
   * Rename built documents named ${documentSrcName} + extension. For
   * example, rename <code>admin-guide/index.epub</code> to
   * <code>admin-guide/OpenAM-Admin-Guide.epub</code>.
   *
   * @param base
   *          Directory under which to find the built documents
   * @param ext
   *          File name extension including the dot, e.g. .pdf
   * @throws MojoExecutionException
   *           Something went wrong renaming files.
   */
  void renameDocuments(String base, String ext)
      throws MojoExecutionException
  {
    String s = File.separator;
    String baseName = FilenameUtils
        .getBaseName(documentSrcName);

    Set<String> docNames = DocUtils.getDocumentNames(
        docbkxSourceDirectory, documentSrcName);
    if (docNames.isEmpty())
    {
      throw new MojoExecutionException("No document names found.");
    }

    for (String docName : docNames)
    {
      String newName = DocUtils.renameDoc(projectName, docName, ext);
      File src = new File(base + s + docName + s + baseName + "." + ext);
      File dest = new File(base + s + docName + s + newName);
      if (newName == "" || !src.renameTo(dest))
      {
        throw new MojoExecutionException("Failed to rename "
            + src.getPath());
      }
    }
  }



  /**
   * Rename a single built document ${documentSrcName} + extension. For example,
   * rename <code>index.pdf</code> to <code>OpenAM-Admin-Guide.pdf</code>.
   *
   * @param file
   *          File to rename, such as <code>index.pdf</code>
   * @param docName
   *          Simple document name such as <code>admin-guide</code>
   * @throws MojoExecutionException
   *           Something went wrong renaming the file.
   */
  void renameDocument(File file, String docName)
      throws MojoExecutionException
  {


    String ext = FilenameUtils.getExtension(file.getName());
    String newName = file.getParent() + File.separator
        + DocUtils.renameDoc(projectName, docName, ext);
    try
    {
      File newFile = new File(newName);
      if (!newFile.exists())
      {
        FileUtils.moveFile(file, newFile);
      }
    }
    catch (IOException e)
    {
      throw new MojoExecutionException("Failed to rename " + file);
    }
  }



  /**
   * FO XSL stylesheet customization file (for PDF, RTF)
   * <p>
   * docbkx-tools element: &lt;foCustomization&gt;
   *
   * @parameter default-value=
   *            "${project.build.directory}/docbkx-stylesheets/fo/coredoc.xsl"
   * @required
   */
  protected File foCustomization;



  /**
   * Prepare built PDF documents for publication. Currently this method does not
   * do anything.
   *
   * @param pdfDir
   *          Directory under which to find the built files
   * @throws MojoExecutionException
   *           Something went wrong updating files.
   */
  void postProcessPDF(String pdfDir) throws MojoExecutionException
  {
    ;
  }



  /**
   * Prepare built RTF documents for publication. Currently this method does not
   * do anything.
   *
   * @param rtfDir
   *          Directory under which to find the built files
   * @throws MojoExecutionException
   *           Something went wrong updating files.
   */
  void postProcessRTF(String rtfDir) throws MojoExecutionException
  {
    ;
  }



  /**
   * Man page XSL stylesheet customization file
   * <p>
   * docbkx-tools element: &lt;manpagesCustomization&gt;
   *
   * @parameter default-value=
   *            "${project.build.directory}/docbkx-stylesheets/man/coredoc.xsl"
   * @required
   */
  protected File manpagesCustomization;

  /**
   * Single page HTML XSL stylesheet customization file
   * <p>
   * docbkx-tools element: &lt;htmlCustomization&gt;
   *
   * @parameter default-value=
   *            "${project.build.directory}/docbkx-stylesheets/html/coredoc.xsl"
   * @required
   */
  protected File singleHTMLCustomization;



  /**
   * Get absolute path to a temporary Olink target database XML document that
   * points to the individual generated Olink DB files. The current
   * implementation works only for single HTML.
   *
   * @return Absolute path to the temporary file
   */
  String getTargetDatabaseDocument() throws MojoExecutionException
  {
    File targetDB = new File(buildDirectory + File.separator
        + "olinkdb-single-html-pages.xml");
    try
    {
      StringBuilder content = new StringBuilder();
      content.append("<?xml version='1.0' encoding='utf-8'?>\n");
      content.append("<!DOCTYPE targetset[\n");

      String targetDbDtd = IOUtils.toString(getClass()
          .getResourceAsStream("/targetdatabase.dtd"));
      content.append(targetDbDtd + "\n");

      Set<String> docNames = DocUtils.getDocumentNames(
          docbkxSourceDirectory, documentSrcName);
      if (docNames.isEmpty())
      {
        throw new MojoExecutionException("No document names found.");
      }

      for (String docName : docNames)
      {
        String SysID = buildDirectory.getAbsolutePath()
            + File.separator + docName + ".target.db";
        content.append("<!ENTITY " + docName + " SYSTEM '" + SysID
            + "'>\n");
      }

      content.append("]>\n");

      content.append("<targetset>\n");
      content
          .append(" <targetsetinfo>Target DB for OpenIDM DocBook content,\n");
      content
          .append(" for use with non-chunked HTML only.</targetsetinfo>\n");
      content.append(" <sitemap>\n");
      content.append("  <dir name='doc'>\n");

      for (String docName : docNames)
      {
        content.append("   <dir name='" + docName + "'>\n");

        String longName = DocUtils
            .renameDoc(projectName, docName, "");
        if (longName == "")
        {
          throw new MojoExecutionException(
              "Failed to get the long name for " + projectName
                  + " and " + docName);
        }

        content.append("    <dir name='" + longName + "'>\n");
        content
            .append("     <document targetdoc='" + docName + "'\n");
        content.append("               baseuri='../" + docName + "/"
            + FilenameUtils.getBaseName(documentSrcName)
            + ".html'>\n");
        content.append("      &" + docName + ";\n");
        content.append("     </document>\n");
        content.append("    </dir>\n");
        content.append("   </dir>\n");
      }
      content.append("  </dir>\n");
      content.append(" </sitemap>\n");
      content.append("</targetset>\n");

      FileUtils.writeStringToFile(targetDB, content.toString());
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(
          "Failed to use temporary file for Olink target database: "
              + e.getMessage());
    }
    return targetDB.getPath();
  }



  /**
   * Chunked HTML XSL stylesheet customization file
   * <p>
   * docbkx-tools element: &lt;htmlCustomization&gt;
   *
   * @parameter default-value=
   *            "${project.build.directory}/docbkx-stylesheets/html/chunked.xsl"
   * @required
   */
  protected File chunkedHTMLCustomization;



  /**
   * Prepare single and chunked HTML for publication.
   * <p>
   * The HTML built by docbkx-tools does not currently include the following,
   * which this method adds.
   * <ul>
   * <li>A DOCTYPE declaration (needed to get Internet Explorer to interpret CSS
   * correctly</li>
   * <li>JavaScript to workaround a long-standing Firefox issue, and the
   * fold/unfold long lines in Screen elements</li>
   * <li>A favicon link</li>
   * <li>JavaScript used by Google Analytics</li>
   * <li>CSS to style the HTML</li>
   * <li>.htaccess files, currently only used to compress the docs when served
   * by Apache HTTPD</li>
   * </ul>
   *
   * @param htmlDir
   *          Directory under which to find HTML output
   * @throws MojoExecutionException
   *           Something went wrong when updating HTML.
   */
  void postProcessHTML(String htmlDir) throws MojoExecutionException
  {
    try
    {
      getLog().info("Editing built HTML...");
      HashMap<String,String> replacements = new HashMap<String,String>();

      String doctype = IOUtils.toString(getClass()
          .getResourceAsStream("/starthtml-doctype.txt"), "UTF-8");
      replacements.put("<html>", doctype);

      String javascript = IOUtils.toString(getClass()
          .getResourceAsStream("/endhead-js-favicon.txt"), "UTF-8");
      replacements.put("</head>", javascript);

      String gascript = IOUtils.toString(getClass()
          .getResourceAsStream("/endbody-ga.txt"), "UTF-8");
      gascript = gascript.replace("ANALYTICS-ID", googleAnalyticsId);
      replacements.put("</body>", gascript);

      HTMLUtils.updateHTML(htmlDir, replacements);

      getLog().info("Adding CSS...");
      File css = new File(buildDirectory.getPath() + File.separator
          + "coredoc.css");
      FileUtils.deleteQuietly(css);
      FileUtils.copyURLToFile(getClass().getResource("/coredoc.css"),
          css);
      HTMLUtils.addCss(htmlDir, css,
          FilenameUtils.getBaseName(documentSrcName) + ".html");

      getLog().info("Adding .htaccess files...");
      File htaccess = new File(buildDirectory.getPath()
          + File.separator + ".htaccess");
      FileUtils.deleteQuietly(htaccess);
      FileUtils.copyURLToFile(getClass().getResource("/.htaccess"),
          htaccess);
      HTMLUtils.addHtaccess(htmlDir, htaccess);
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(
          "Failed to update output HTML correctly: " + e.getMessage());
    }
  }



  /**
   * Directory where fonts and font metrics are stored
   *
   * @parameter default-value="${project.build.directory}/fonts"
   *            expression="${fontsDirectory}"
   * @required
   */
  protected File fontsDirectory;

  /**
   * Should WinAnsi encoding be used for embedded fonts?
   *
   * @parameter default-value="false"
   * @required
   */
  protected String ansi; // TODO: Check for OPENDJ-316 regression

  /**
   * Are these draft documents, rather than final documents?
   * <p>
   * docbkx-tools element: &lt;draftMode&gt;
   *
   * @parameter default-value="yes" expression="${isDraftMode}"
   * @required
   */
  protected String isDraftMode;

  /**
   * For draft mode, URL to the background watermark image.
   * <p>
   * docbkx-tools element: &lt;draftWatermarkImage&gt;
   *
   * @parameter
   *            default-value="http://docbook.sourceforge.net/release/images/draft.png"
   *            expression="${draftWatermarkURL}"
   * @required
   */
  protected String draftWatermarkURL;

  /**
   * Should &lt;programlisting&gt; content have syntax highlighting?
   * <p>
   * docbkx-tools element: &lt;highlightSource&gt;
   *
   * @parameter default-value="1" expression="${useSyntaxHighlighting}"
   * @required
   */
  protected String useSyntaxHighlighting;

  /**
   * Should sections have numeric labels?
   * <p>
   * docbkx-tools element: &lt;sectionAutolabel&gt;
   *
   * @parameter default-value="true" expression="${areSectionsAutolabeled}"
   * @required
   */
  protected String areSectionsAutolabeled;

  /**
   * Should section labels include parent numbers, like 1.1, 1.2, 1.2.1, 1.2.2?
   * <p>
   * docbkx-tools element: &lt;sectionLabelIncludesComponentLabel&gt;
   *
   * @parameter default-value="true"
   *            expression="${doesSectionLabelIncludeComponentLabel}
   * @required
   */
  protected String doesSectionLabelIncludeComponentLabel;

  /**
   * Should documents be allowed to include other documents?
   * <p>
   * docbkx-tools element: &lt;xincludeSupported&gt;
   *
   * @parameter default-value="true" expression="${isXincludeSupported}"
   * @required
   */
  protected String isXincludeSupported;



  /**
   * Enclose methods to run plugins.
   */
  class Executor extends MojoExecutor
  {
    /**
     * Prepare Apache FOP for output formats like PDF.
     *
     * @throws MojoExecutionException
     */
    void prepareFOP() throws MojoExecutionException
    {
      String fontsDir = FilenameUtils
          .separatorsToUnix(fontsDirectory.getPath());
      executeMojo(
          plugin(
              groupId("com.agilejava.docbkx"),
              artifactId("docbkx-fop-support"),
              version(docbkxVersion)),
          goal("generate"),
          configuration(
              element(name("ansi"), ansi),
              element(name("sourceDirectory"), fontsDir),
              element(name("targetDirectory"), fontsDir)),
          executionEnvironment(project, session, pluginManager));
    }



    /**
     * Returns element array for common configuration of all executions of the
     * docbkx-maven-plugin.
     *
     * @returns Configuration applicable to all executions
     */
    ArrayList<MojoExecutor.Element> getBaseConfiguration()
    {
      ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();

      cfg.add(element(name("draftMode"), isDraftMode));
      cfg.add(element(name("draftWatermarkImage"), draftWatermarkURL));
      cfg.add(element(name("highlightSource"), useSyntaxHighlighting));
      cfg.add(element(name("sectionAutolabel"),
          areSectionsAutolabeled));
      cfg.add(element(name("sectionLabelIncludesComponentLabel"),
          doesSectionLabelIncludeComponentLabel));
      cfg.add(element(name("xincludeSupported"), isXincludeSupported));
      cfg.add(element(name("sourceDirectory"),
          FilenameUtils.separatorsToUnix(docbkxSourceDirectory.getPath())));

      return cfg;
    }



    /**
     * Prepare Olink database files. TODO: Make this work for more than single
     * page HTML output.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @throws MojoExecutionException
     */
    void buildOlinkDB(
        ArrayList<MojoExecutor.Element> baseConfiguration)
        throws MojoExecutionException
    {
      ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
      cfg.add(element(name("xincludeSupported"), isXincludeSupported));
      cfg.add(element(name("sourceDirectory"),
          FilenameUtils.separatorsToUnix(docbkxSourceDirectory.getPath())));

      Set<String> docNames = DocUtils.getDocumentNames(
          docbkxSourceDirectory, documentSrcName);
      if (docNames.isEmpty())
      {
        throw new MojoExecutionException("No document names found.");
      }

      for (String docName : docNames)
      {
        cfg.add(element(name("includes"), docName + "/"
            + documentSrcName));
        cfg.add(element(name("collectXrefTargets"), "only"));
        cfg.add(element(name("targetsFilename"),
            FilenameUtils.separatorsToUnix(buildDirectory.getPath())
            + "/"
            + docName + ".target.db"));

        executeMojo(
            plugin(
                groupId("com.agilejava.docbkx"),
                artifactId("docbkx-maven-plugin"),
                version(docbkxVersion)),
            goal("generate-html"),
            configuration(cfg.toArray(new Element[0])),
            executionEnvironment(project, session, pluginManager));

        File outputDir = new File(docbkxOutputDirectory, "html"
            + File.separator + docName);
        try
        {
          FileUtils.deleteDirectory(outputDir);
        }
        catch (IOException e)
        {
          throw new MojoExecutionException("Cannot delete "
              + outputDir);
        }
      }
    }



    /**
     * Build EPUB documents from DocBook XML sources.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @throws MojoExecutionException
     */
    void buildEPUB(ArrayList<MojoExecutor.Element> baseConfiguration)
        throws MojoExecutionException
    {

      ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
      cfg.addAll(baseConfiguration);
      cfg.add(element(name("includes"), "*/" + documentSrcName));
      cfg.add(element(name("epubCustomization"),
          FilenameUtils.separatorsToUnix(epubCustomization.getPath())));

      // Copy images from source to build. DocBook XSL does not copy the
      // images, because XSL does not have a facility for copying files.
      // Unfortunately, neither does docbkx-tools.

      String baseName = FilenameUtils
          .getBaseName(documentSrcName);

      Set<String> docNames = DocUtils.getDocumentNames(
          docbkxSourceDirectory, documentSrcName);
      if (docNames.isEmpty())
      {
        throw new MojoExecutionException("No document names found.");
      }

      for (String docName : docNames)
      {
        File srcDir = new File(docbkxSourceDirectory, docName
            + File.separator + "images");
        File destDir = new File(docbkxOutputDirectory, "epub"
            + File.separator + docName + File.separator + baseName
            + File.separator + "images");
        try
        {
          FileUtils.copyDirectory(srcDir, destDir);
        }
        catch (IOException e)
        {
          throw new MojoExecutionException(
              "Failed to copy images from " + srcDir + " to "
                  + destDir);
        }
      }

      executeMojo(
          plugin(
              groupId("com.agilejava.docbkx"),
              artifactId("docbkx-maven-plugin"),
              version(docbkxVersion)),
          goal("generate-epub"),
          configuration(cfg.toArray(new Element[0])),
          executionEnvironment(project, session, pluginManager));
    }



    /**
     * Build FO documents from DocBook XML sources.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @param format
     *          Specific output format (pdf, rtf)
     * @throws MojoExecutionException
     */
    void buildFO(ArrayList<MojoExecutor.Element> baseConfiguration,
        String format) throws MojoExecutionException
    {
      if (!(format.equalsIgnoreCase("pdf") || format
          .equalsIgnoreCase("rtf")))
      {
        throw new MojoExecutionException("Output format " + format
            + " is not supported." + " Use either pdf or rtf.");
      }

      ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
      cfg.addAll(baseConfiguration);
      cfg.add(element(name("foCustomization"),
          FilenameUtils.separatorsToUnix(foCustomization.getPath())));

      // If you update this list, also see copyFonts().
      String fontDir = FilenameUtils.separatorsToUnix(fontsDirectory.getPath());
      cfg.add(element(
          name("fonts"),
          element(
              name("font"),
              element(name("name"), "DejaVuSans"),
              element(name("style"), "normal"),
              element(name("weight"), "normal"),
              element(name("embedFile"), fontDir + "/DejaVuSans.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSans-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSans"),
              element(name("style"), "normal"),
              element(name("weight"), "bold"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSansCondensed-Bold.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSansCondensed-Bold-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSans"),
              element(name("style"), "italic"),
              element(name("weight"), "normal"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSans-Oblique.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSans-Oblique-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSans"),
              element(name("style"), "italic"),
              element(name("weight"), "bold"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSansCondensed-BoldOblique.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSansCondensed-BoldOblique-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSansMono"),
              element(name("style"), "normal"),
              element(name("weight"), "normal"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSansMono.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSansMono-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSansMono"),
              element(name("style"), "normal"),
              element(name("weight"), "bold"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSansMono-Bold.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSansMono-Bold-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSansMono"),
              element(name("style"), "italic"),
              element(name("weight"), "normal"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSansMono-Oblique.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSansMono-Oblique-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSansMono"),
              element(name("style"), "italic"),
              element(name("weight"), "bold"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSansMono-BoldOblique.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSansMono-BoldOblique-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSerif"),
              element(name("style"), "normal"),
              element(name("weight"), "normal"),
              element(name("embedFile"), fontDir + "/DejaVuSerif.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSerif-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSerif"),
              element(name("style"), "normal"),
              element(name("weight"), "bold"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSerifCondensed-Bold.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSerifCondensed-Bold-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSerif"),
              element(name("style"), "italic"),
              element(name("weight"), "normal"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSerif-Italic.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSerif-Italic-metrics.xml")),
          element(
              name("font"),
              element(name("name"), "DejaVuSerif"),
              element(name("style"), "italic"),
              element(name("weight"), "bold"),
              element(name("embedFile"), fontDir
                  + "/DejaVuSerifCondensed-BoldItalic.ttf"),
              element(name("metricsFile"), fontDir
                  + "/DejaVuSerifCondensed-BoldItalic-metrics.xml"))));

      Set<String> docNames = DocUtils.getDocumentNames(
          docbkxSourceDirectory, documentSrcName);
      if (docNames.isEmpty())
      {
        throw new MojoExecutionException("No document names found.");
      }

      for (String docName : docNames)
      {
        cfg.add(element(name("includes"), docName + "/"
            + documentSrcName));

        executeMojo(
            plugin(
                groupId("com.agilejava.docbkx"),
                artifactId("docbkx-maven-plugin"),
                version(docbkxVersion)),
            goal("generate-" + format),
            configuration(cfg.toArray(new Element[0])),
            executionEnvironment(project, session, pluginManager));

        File file = new File(docbkxOutputDirectory, format
            + File.separator
            + FilenameUtils.getBaseName(documentSrcName) + "."
            + format);
        renameDocument(file, docName);
      }
    }



    /**
     * Build PDF documents from DocBook XML sources.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @throws MojoExecutionException
     */
    void buildPDF(ArrayList<MojoExecutor.Element> baseConfiguration)
        throws MojoExecutionException
    {
      buildFO(baseConfiguration, "pdf");
    }



    /**
     * Build RTF documents from DocBook XML sources.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @throws MojoExecutionException
     */
    void buildRTF(ArrayList<MojoExecutor.Element> baseConfiguration)
        throws MojoExecutionException
    {
      buildFO(baseConfiguration, "rtf");
    }



    /**
     * Build reference manual pages from DocBook XML sources.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @throws MojoExecutionException
     */
    void buildManpages(
        ArrayList<MojoExecutor.Element> baseConfiguration)
        throws MojoExecutionException
    {
      ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
      cfg.addAll(baseConfiguration);
      cfg.add(element(name("includes"), "*/" + documentSrcName));
      cfg.add(element(
          name("manpagesCustomization"),
          FilenameUtils.separatorsToUnix(manpagesCustomization.getPath())));

      executeMojo(
          plugin(
              groupId("com.agilejava.docbkx"),
              artifactId("docbkx-maven-plugin"),
              version(docbkxVersion)),
          goal("generate-manpages"),
          configuration(cfg.toArray(new Element[0])),
          executionEnvironment(project, session, pluginManager));
    }



    /**
     * Build single page HTML from DocBook XML sources.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @throws MojoExecutionException
     */
    void buildSingleHTML(
        ArrayList<MojoExecutor.Element> baseConfiguration)
        throws MojoExecutionException
    {
      ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
      cfg.addAll(baseConfiguration);
      cfg.add(element(name("includes"), "*/" + documentSrcName));
      cfg.add(element(name("chunkedOutput"), "false"));
      cfg.add(element(name("htmlCustomization"),
          FilenameUtils.separatorsToUnix(singleHTMLCustomization.getPath())));
      cfg.add(element(name("targetDatabaseDocument"),
          getTargetDatabaseDocument()));

      // Copy images from source to build. DocBook XSL does not copy the
      // images, because XSL does not have a facility for copying files.
      // Unfortunately, neither does docbkx-tools.

      Set<String> docNames = DocUtils.getDocumentNames(
          docbkxSourceDirectory, documentSrcName);
      if (docNames.isEmpty())
      {
        throw new MojoExecutionException("No document names found.");
      }

      for (String docName : docNames)
      {
        File srcDir = new File(docbkxSourceDirectory, docName
            + File.separator + "images");
        File destDir = new File(docbkxOutputDirectory, "html"
            + File.separator + docName + File.separator + "images");
        try
        {
          FileUtils.copyDirectory(srcDir, destDir);
        }
        catch (IOException e)
        {
          throw new MojoExecutionException(
              "Failed to copy images from " + srcDir + " to "
                  + destDir);
        }
      }

      executeMojo(
          plugin(
              groupId("com.agilejava.docbkx"),
              artifactId("docbkx-maven-plugin"),
              version(docbkxVersion)),
          goal("generate-html"),
          configuration(cfg.toArray(new Element[0])),
          executionEnvironment(project, session, pluginManager));
    }



    /**
     * Build chunked HTML pages from DocBook XML sources.
     *
     * @param baseConfiguration
     *          Common configuration for all executions
     * @throws MojoExecutionException
     */
    void buildChunkedHTML(
        ArrayList<MojoExecutor.Element> baseConfiguration)
        throws MojoExecutionException
    {
      ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
      cfg.addAll(baseConfiguration);
      cfg.add(element(name("includes"), "*/" + documentSrcName));
      cfg.add(element(name("chunkedOutput"), "true"));
      cfg.add(element(name("htmlCustomization"),
          FilenameUtils.separatorsToUnix(chunkedHTMLCustomization.getPath())));

      // Copy images from source to build. DocBook XSL does not copy the
      // images, because XSL does not have a facility for copying files.
      // Unfortunately, neither does docbkx-tools.

      String baseName = FilenameUtils
          .getBaseName(documentSrcName);

      Set<String> docNames = DocUtils.getDocumentNames(
          docbkxSourceDirectory, documentSrcName);
      if (docNames.isEmpty())
      {
        throw new MojoExecutionException("No document names found.");
      }

      for (String docName : docNames)
      {
        File srcDir = new File(docbkxSourceDirectory, docName
            + File.separator + "images");
        File destDir = new File(docbkxOutputDirectory, "html"
            + File.separator + docName + File.separator + baseName
            + File.separator + "images");
        try
        {
          FileUtils.copyDirectory(srcDir, destDir);
        }
        catch (IOException e)
        {
          throw new MojoExecutionException(
              "Failed to copy images from " + srcDir + " to "
                  + destDir);
        }
      }

      executeMojo(
          plugin(
              groupId("com.agilejava.docbkx"),
              artifactId("docbkx-maven-plugin"),
              version(docbkxVersion)),
          goal("generate-html"),
          configuration(cfg.toArray(new Element[0])),
          executionEnvironment(project, session, pluginManager));
    }
  }
}
