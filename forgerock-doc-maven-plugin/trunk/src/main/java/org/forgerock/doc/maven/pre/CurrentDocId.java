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
 *     Copyright 2014 ForgeRock AS
 *
 */

package org.forgerock.doc.maven.pre;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.forgerock.doc.maven.AbstractDocbkxMojo;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Replace {@code CURRENT.DOCID#} with the current document ID + #.
 * The current document ID is used to resolve olinks.
 */
public class CurrentDocId {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public CurrentDocId(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    private static final String CURRENT_DOCID = "CURRENT.DOCID#";

    /**
     * Replace {@code CURRENT.DOCID#} with the current document ID + #.
     *
     * @throws MojoExecutionException Failed to handle an XML source file.
     */
    public void execute() throws MojoExecutionException {

        final Set<String> docNames = m.getDocNames();
        final String sourceEncoding = m.getProject().getProperties()
                .getProperty("project.build.sourceEncoding", "UTF-8");

        for (String docName : docNames) {

            File documentDirectory = new File(m.getDocbkxModifiableSourcesDirectory(), docName);
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(documentDirectory);
            scanner.setIncludes(new String[] { "**/*.xml" });
            scanner.addDefaultExcludes();
            scanner.scan();

            for (String docFile : scanner.getIncludedFiles()) {
                try {
                    File documentFile = new File(documentDirectory, docFile);
                    String content = FileUtils.fileRead(documentFile, sourceEncoding);
                    String newContent = StringUtils.replace(content, CURRENT_DOCID, docName + "#");
                    FileUtils.fileWrite(documentFile, sourceEncoding, newContent);
                } catch (IOException e) {
                    throw new MojoExecutionException(e.getMessage(), e);
                }
            }
        }
    }
}
