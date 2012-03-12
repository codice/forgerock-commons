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
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;



/**
 * AbstractMojo implementation for building core documentation from <a
 * href="http://www.docbook.org/tdg51/en/html/docbook.html">DocBook XML</a>
 * using <a href="http://code.google.com/p/docbkx-tools/">docbkx-tools</a>
 */
abstract class AbstractBuildMojo extends AbstractMojo
{
  /**
   * Executions seem to hit an NPE when the version is not specified.
   */
  protected final String docbkxVersion = "2.0.14"; // FIXME!
  protected final String resourcesVersion = "2.5"; // FIXME!

  /**
   * Short name of the project, such as OpenAM, OpenDJ, OpenIDM.
   *
   * @parameter expression="${projectName}"
   * @required
   */
  protected String projectName;

  /**
   * Google Analytics identifier for the project.
   *
   * @parameter expression="${googleAnalyticsId}"
   * @required
   */
  protected String googleAnalyticsId;

  /**
   * Do not process these formats. Choices include: epub, html, man, pdf, rtf.
   *
   * @parameter
   */
  protected List<String> excludes;

  /**
   * Return the list of formats not to process.
   *
   * @return Formats not to process (choices: epub, html, man, pdf, rtf)
   */
  public List<String> getExcludes() {
    return excludes;
  }

  /**
   * Set the list of formats not to process (epub, html, man, pdf, rtf).
   */
  public void setExcludes(List<String> excludes) {
    this.excludes = excludes;
  }

  /**
   * Base directory for DocBook XML source files.
   *
   * @parameter default-value="${basedir}/src/main/docbkx"
   *            expression="${docbkxSourceDirectory}"
   * @required
   */
  protected File docbkxSourceDirectory;

  /**
   * Base directory for built documentation.
   *
   * @parameter default-value="${project.build.directory}/docbkx"
   *            expression="${docbkxOutputDirectory}"
   * @required
   */
  protected File docbkxOutputDirectory;

  /**
   * Target directory for this plugin.
   *
   * @parameter default-value="${project.build.directory}"
   * @required
   */
  protected File buildDirectory;

  /**
   * The Maven Project Object.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project;

  /**
   * The Maven Session Object.
   *
   * @parameter expression="${session}"
   * @required
   * @readonly
   */
  protected MavenSession session;

  /**
   * The Maven PluginManager Object.
   *
   * @component
   * @required
   */
  protected BuildPluginManager pluginManager;

  /**
   * Top-level DocBook documents included in the documentation set such as
   * books, articles, and references share a common entry point, which is a file
   * having the name specified by this element.
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
   * @parameter default-value="index.xml"
   *            expression="${documentSrcName}"
   * @required
   */
  protected String documentSrcName;
}
