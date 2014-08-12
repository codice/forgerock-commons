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

package org.forgerock.doc.maven.build;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.pre.Fop;
import org.forgerock.doc.maven.utils.NameUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Build FO output formats.
 */
public class Fo {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Fo(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Supported FO formats include "pdf" and "rtf".
     */
    private String format = "pdf";

    /**
     * Get the format.
     * Defaults to PDF unless the format has been set to RTF.
     *
     * @return The format, either "pdf" or "rtf".
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the format to PDF or RTF.
     * Defaults to PDF unless RTF is specified (case does not matter).
     *
     * @param format Either {@code pdf} or {@code rtf}.
     */
    public void setFormat(final String format) {
        if (format.equalsIgnoreCase("rtf")) {
            this.format = "rtf";
        } else {
            this.format = "pdf";
        }
    }

    /**
     * Build documents from DocBook XML sources.
     *
     * @throws MojoExecutionException Failed to build output.
     */
    public void execute() throws MojoExecutionException {
        Executor executor = new Executor();
        executor.prepareOlinkDB();
        executor.build();
    }

    /**
     * Get absolute path to a temporary Olink target database XML document
     * that points to the individual generated Olink DB files, for FO (PDF, RTF).
     *
     * @return Absolute path to the temporary file
     * @throws MojoExecutionException Could not write target DB file.
     */
    final String getTargetDB() throws MojoExecutionException {
        File targetDB = new File(m.getBuildDirectory(), "olinkdb-" + getFormat() + ".xml");

        try {
            StringBuilder content = new StringBuilder();
            content.append("<?xml version='1.0' encoding='utf-8'?>\n")
                    .append("<!DOCTYPE targetset[\n");

            String targetDbDtd = IOUtils.toString(getClass()
                    .getResourceAsStream("/targetdatabase.dtd"));
            content.append(targetDbDtd).append("\n");

            final Set<String> docNames = m.getDocNames();

            for (String docName : m.getDocNames()) {
/*  <targetsFilename> is ignored with docbkx-tools 2.0.15.
                String sysId = getBuildDirectory().getAbsolutePath()
                        + File.separator + docName + "-" + extension + ".target.db";
*/
                String sysId = m.getBaseDir().getAbsolutePath()
                        + "/target/docbkx/" + getFormat() + "/" + docName
                        + "/index.fo.target.db";

                content.append("<!ENTITY ").append(docName)
                        .append(" SYSTEM '").append(sysId).append("'>\n");
            }

            content.append("]>\n")

                    .append("<targetset>\n")
                    .append(" <targetsetinfo>Target DB for DocBook content,\n")
                    .append(" for use with ")
                    .append(getFormat().toUpperCase())
                    .append(" only.</targetsetinfo>\n")
                    .append(" <sitemap>\n")
                    .append("  <dir name='doc'>\n");

            final String version = m.getReleaseVersion();
            for (String docName : docNames) {
                String fileName = NameUtils.renameDoc(
                        m.getProjectName(), docName, version, getFormat());

                content.append("   <document targetdoc='").append(docName).append("'")
                        .append("             baseuri='").append(fileName).append("'>")
                        .append("    &").append(docName).append(";")
                        .append("   </document>\n");
            }
            content.append("  </dir>\n")
                    .append(" </sitemap>\n")
                    .append("</targetset>\n");

            FileUtils.writeStringToFile(targetDB, content.toString());
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to write link target database: " + e.getMessage());
        }
        return targetDB.getPath();
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Prepare olink target database from DocBook XML sources.
         *
         * @throws MojoExecutionException Failed to build target database.
         */
        void prepareOlinkDB() throws MojoExecutionException {

            for (String docName : m.getDocNames()) {
                ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
                cfg.addAll(m.getBaseConfiguration());
                cfg.add(element(name("xincludeSupported"), m.isXincludeSupported()));
                cfg.add(element(name("sourceDirectory"),
                        m.path(m.getDocbkxModifiableSourcesDirectory())));
                cfg.add(element(name("fop1Extensions"), "1"));
                cfg.add(element(name("collectXrefTargets"), "yes"));
                if (getFormat().equalsIgnoreCase("pdf")) {
                    cfg.add(element(name("insertOlinkPdfFrag"), "1"));
                }
                cfg.add(element(name("includes"), docName + "/" + m.getDocumentSrcName()));
                cfg.add(element(name("currentDocid"), docName));

                // <targetsFilename> is ignored with docbkx-tools 2.0.15,
                // but not with 2.0.14.

                // The following configuration should be kept
                // for versions of docbkx-tools that honor <targetsFilename>.
                cfg.add(element(
                        name("targetsFilename"),
                        m.path(m.getBuildDirectory()) + "/" + docName + "-" + getFormat() + ".target.db"));

                // Due to https://code.google.com/p/docbkx-tools/issues/detail?id=112
                // RTF generation does not work with docbkx-tools 2.0.15.
                // If the format is RTF, stick with 2.0.14 for now.
                // TODO: Remove this sick hack when docbkx-tools #112 is fixed.
                String docbkxVersion = m.getDocbkxVersion();
                if (getFormat().equalsIgnoreCase("rtf")) {
                    docbkxVersion = "2.0.14";
                }

                executeMojo(
                        plugin(groupId("com.agilejava.docbkx"),
                                artifactId("docbkx-maven-plugin"),
                                version(docbkxVersion)),
                        goal("generate-" + getFormat()),
                        configuration(cfg.toArray(new Element[cfg.size()])),
                        executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager())
                );

                File outputDir = new File(m.getBaseDir(),
                        "target" + File.separator + "docbkx" + File.separator
                                + getFormat() + File.separator + docName);

                // <targetsFilename> is ignored with docbkx-tools 2.0.15,
                // but not with 2.0.14.
                // The following output directory should be where the files are
                // for versions of docbkx-tools that honor <targetsFilename>.
                if (!outputDir.exists()) {
                    outputDir = new File(m.getDocbkxOutputDirectory(),
                            getFormat() + File.separator + docName);
                }

                try {
                    String[] extensions = {"fo", getFormat()};
                    Iterator<File> files =
                            FileUtils.iterateFiles(outputDir, extensions, true);
                    while (files.hasNext()) {
                        FileUtils.forceDelete(files.next());
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(
                            "Cannot delete a file: " + e.getMessage());
                }
            }
        }

        /**
         * Build documents from DocBook XML sources.
         *
         * @throws MojoExecutionException Failed to build the output.
         */
        void build() throws MojoExecutionException {

            for (String docName : m.getDocNames()) {
                ArrayList<MojoExecutor.Element> cfg = new ArrayList<MojoExecutor.Element>();
                cfg.addAll(m.getBaseConfiguration());
                cfg.add(element(name("foCustomization"), m.path(m.getFoCustomization())));
                cfg.add(element(name("fop1Extensions"), "1"));
                if (getFormat().equalsIgnoreCase("pdf")) {
                    cfg.add(element(name("insertOlinkPdfFrag"), "1"));
                }
                cfg.add(element(name("targetDatabaseDocument"), getTargetDB()));
                cfg.add(element(name("targetDirectory"),
                        m.path(m.getDocbkxOutputDirectory()) + "/" + getFormat()));

                final String fontDir = m.path(m.getFontsDirectory());
                cfg.add(Fop.getFontsElement(fontDir));

                cfg.add(element(name("includes"), docName + "/" + m.getDocumentSrcName()));
                cfg.add(element(name("currentDocid"), docName));

                // Due to https://code.google.com/p/docbkx-tools/issues/detail?id=112
                // RTF generation does not work with docbkx-tools 2.0.15.
                // If the format is RTF, stick with 2.0.14 for now.
                // TODO: Remove this sick hack when docbkx-tools #112 is fixed.
                String docbkxVersion = m.getDocbkxVersion();
                if (format.equalsIgnoreCase("rtf")) {
                    docbkxVersion = "2.0.14";
                }

                executeMojo(
                        plugin(
                                groupId("com.agilejava.docbkx"),
                                artifactId("docbkx-maven-plugin"),
                                version(docbkxVersion),
                                dependencies(
                                        dependency(
                                                groupId("net.sf.offo"),
                                                artifactId("fop-hyph"),
                                                version("1.2")))),
                        goal("generate-" + getFormat()),
                        configuration(cfg.toArray(new Element[cfg.size()])),
                        executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));

                // Avoid each new document overwriting the last.
                File file = new File(m.getDocbkxOutputDirectory(), getFormat()
                        + File.separator
                        + FilenameUtils.getBaseName(m.getDocumentSrcName()) + "."
                        + getFormat());
                NameUtils.renameDocument(file, docName, m.getProjectName());
            }
        }
    }
}
