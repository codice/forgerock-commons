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

package org.forgerock.doc.maven.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Set;

/**
 * Copy images from source to destination.
 */
public final class ImageCopier {

    /**
     * Support a subset of formats described in the documentation for the <a
     * href="http://www.docbook.org/tdg/en/html/imagedata.html">ImageData</a>
     * element.
     */
    private static final String[] IMAGE_FILE_SUFFIXES =
    {".bmp", ".eps", ".gif", ".jpeg", ".jpg", ".png", ".svg", ".tiff"};

    /**
     * Copy images from source to destination.
     *
     * <p>
     *
     * DocBook XSL does not copy the images,
     * because XSL does not have a facility for copying files.
     * Unfortunately, neither does docbkx-tools.
     *
     * @param docType         Type of output document,
     *                        such as {@code epub} or {@code html}
     * @param baseName        Directory name to add, such as {@code index}.
     *                        Leave null or empty when not adding a directory name.
     * @param documentSrcName Top-level DocBook XML document source name,
     *                        such as {@code index.xml}.
     * @param sourceDirectory Base directory for DocBook XML sources.
     * @param outputDirectory Base directory where the output is found.
     *
     * @throws MojoExecutionException Something went wrong copying images.
     */
    public static void copyImages(final String docType,
                                  final String baseName,
                                  final String documentSrcName,
                                  final File sourceDirectory,
                                  final File outputDirectory)
            throws MojoExecutionException {

        if (docType == null) {
            throw new MojoExecutionException("Type of output document must not be null.");
        }


        if (documentSrcName == null) {
            throw new MojoExecutionException(
                    "Top-level DocBook XML document source name must not be null.");
        }

        Set<String> docNames = NameUtils.getDocumentNames(
                sourceDirectory, documentSrcName);
        if (docNames.isEmpty()) {
            throw new MojoExecutionException("No document names found.");
        }

        String s = File.separator;
        String extra = "";
        if (!(baseName == null) && !baseName.equalsIgnoreCase("")) {
            extra = s + baseName;
        }

        FileFilter onlyImages = new SuffixFileFilter(IMAGE_FILE_SUFFIXES);

        for (String docName : docNames) {

            // Copy images specific to the document.
            File srcDir = new File(sourceDirectory, docName + s + "images");
            File destDir = new File(outputDirectory, docType + s + docName + extra + s + "images");
            try {
                if (srcDir.exists()) {
                    FileUtils.copyDirectory(srcDir, destDir, onlyImages);
                }
            } catch (IOException e) {
                throw new MojoExecutionException(
                        "Failed to copy images from " + srcDir + " to " + destDir);
            }

            // Copy any shared images.
            String shared = "shared" + s + "images";
            srcDir = new File(sourceDirectory, shared);
            destDir = new File(outputDirectory, docType + s + docName + extra + s + shared);
            try {
                if (srcDir.exists()) {
                    FileUtils.copyDirectory(srcDir, destDir, onlyImages);
                }
            } catch (IOException ioe) {
                throw new MojoExecutionException(
                        "Failed to copy images from " + srcDir + " to " + destDir);
            }
        }
    }

    private ImageCopier() {
        // Not used.
    }
}
