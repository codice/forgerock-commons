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
 *     Copyright 2013 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;


/**
 * Adapt source files in preparation for output generation.
 *
 * @Checkstyle:ignoreFor 2
 * @goal prepare
 * @phase pre-site
 */
public class PrepareSourcesMojo extends AbstractBuildMojo {

    /**
     * Transform ImageData elements in the XML and set DPI on PNGs.
     *
     * @throws MojoExecutionException Failed to transform an XML file or failed to set DPI on a PNG.
     */
    public void execute() throws MojoExecutionException {
        if (!doUseGeneratedSources()) {
            throw new MojoExecutionException("<useGeneratedSources> must"
                    + " be set to true as preparing sources changes them.");
        }

        // Are there already generated sources? If not, copy sources over.
        final File generatedSourceDirectory = getDocbkxGeneratedSourceDirectory();
        if (!generatedSourceDirectory.isDirectory()) {
            final File sourceDir = getDocbkxSourceDirectory();
            try {
                FileUtils.copyDirectory(sourceDir, generatedSourceDirectory);
            } catch (IOException ie) {
                throw new MojoExecutionException("Failed to copy sources", ie);
            }
        }

        // Update ImageData elements in generated sources.
        try {

            getLog().info("Transforming ImageData elements in XML files:");
            for (File file : transformImageData(generatedSourceDirectory)) {
                getLog().info("\t" + file.getPath());
            }

        } catch (IOException ie) {
            throw new MojoExecutionException("Failed to transform ImageData"
                    + " element in XML file", ie);
        }

        // Set DPI on PNGs.
        try {

            getLog().info("Setting DPI in the following PNG files:");
            for (File image : FileUtils.listFiles(
                    generatedSourceDirectory,
                    new WildcardFileFilter("*.png"),
                    TrueFileFilter.INSTANCE)) {
                PNGUtils.setDPI(image);
                getLog().info("\t" + image.getPath());
            }

        } catch (IOException ie) {
            throw new MojoExecutionException("Failed to set DPI in PNG file", ie);
        }

    }

    /**
     * Update ImageData elements in XML files under {@code xmlSourceDirectory}.
     *
     * @param xmlSourceDirectory Find XML under here, recursively.
     * @return List of XML files transformed.
     * @throws IOException Something went wrong transforming an XML file.
     */
    private List<File> transformImageData(final File xmlSourceDirectory) throws IOException {
        // Match normal directories, and XML files.
        IOFileFilter dirFilter = FileFilterUtils
                .and(FileFilterUtils.directoryFileFilter(),
                        HiddenFileFilter.VISIBLE);
        IOFileFilter fileFilter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".xml"));
        FileFilter filterToMatch = FileFilterUtils.or(dirFilter, fileFilter);

        // Update XML files.
        ImageDataTransformer idt = new ImageDataTransformer(filterToMatch);
        return idt.update(xmlSourceDirectory);
    }
}
