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
 *     Copyright 2013-2014 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

import org.twdata.maven.mojoexecutor.MojoExecutor;

/**
 * Adapt source files in preparation for output generation.
 *
 * @Checkstyle:ignoreFor 2
 * @goal prepare
 * @phase pre-site
 */
public class PrepareSourcesMojo extends AbstractBuildMojo {

    /**
     * DocBook XML sources found here.
     */
    private File sourceDirectory;

    /**
     * Maximum height for PNG images used in PDF, in inches.
     * @parameter default-value="5" property="maxImageHeightInInches"
     * @required
     */
    private int maxImageHeightInInches;

    /**
     * Get maximum height for PNG images used in PDF, in inches.
     * @return Maximum height for PNG images used in PDF, in inches.
     */
    public int getMaxImageHeightInInches() {
        return maxImageHeightInInches;
    }

    /**
     * Transform ImageData elements in the XML, generate PNG images from
     * <a href="http://plantuml.sourceforge.net/">PlantUML</a> text files,
     * and set DPI on PNGs.
     *
     * @throws MojoExecutionException Failed to transform an XML file or failed to set DPI on a PNG.
     */
    public void execute() throws MojoExecutionException {

        if (!doUseFilteredSources()) {
            throw new MojoExecutionException("This version of the doc build"
                    + " plugin requires that you use filtered sources.");
        }

        if (getJCiteOutputDirectory().exists()) {
            sourceDirectory = getJCiteOutputDirectory();
        } else {
            sourceDirectory = getFilteredDocbkxSourceDirectory();
        }

        if (sourceDirectory == null || !sourceDirectory.exists()) {
            throw new MojoExecutionException("Cannot prepare sources.\n"
                + "Source directory " + sourceDirectory + " is missing.");
        }

        // Update ImageData elements in generated sources.
        try {

            getLog().info("Transforming ImageData elements in XML files:");
            for (File file : transformImageData(sourceDirectory)) {
                getLog().info("\t" + file.getPath());
            }

        } catch (IOException ie) {
            throw new MojoExecutionException("Failed to transform ImageData"
                    + " element in XML file", ie);
        }

        // Generate PNG images from PlantUML text files in generated sources.
        Executor exec = new Executor();
        getLog().info("Generating PNG images from PlantUML text files:");
        exec.runPlantUML();

        // Set DPI on PNGs.
        try {

            getLog().info("Setting DPI in the following PNG files:");
            for (File image : FileUtils.listFiles(
                    sourceDirectory,
                    new WildcardFileFilter("*.png"),
                    TrueFileFilter.INSTANCE)) {
                PNGUtils.setSafeDpi(image, maxImageHeightInInches);
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

    class Executor extends MojoExecutor {

        /**
         * Generate PNG images next to PlantUML text files in generated sources.
         *
         * @throws MojoExecutionException Something went wrong generating images.
         */
        void runPlantUML() throws MojoExecutionException {
            final String directory = FilenameUtils.separatorsToUnix(
                    sourceDirectory.getPath());

            executeMojo(
                    plugin(
                            groupId("com.github.jeluard"),
                            artifactId("maven-plantuml-plugin"),
                            version("7940"),
                            dependencies(
                                    dependency(
                                            groupId("net.sourceforge.plantuml"),
                                            artifactId("plantuml"),
                                            version("7940")))),
                    goal("generate"),
                    configuration(
                            element(name("sourceFiles"),
                                    element(name("directory"), directory),
                                    element(name("includes"),
                                            element(name("include"), "**/*.txt"))),
                            element(name("outputInSourceDirectory"), "true"),
                            element(name("verbose"), "true")),
                    executionEnvironment(
                            getProject(),
                            getSession(),
                            getPluginManager()));

        }
    }
}
