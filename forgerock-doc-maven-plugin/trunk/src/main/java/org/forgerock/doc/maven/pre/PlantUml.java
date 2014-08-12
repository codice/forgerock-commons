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

package org.forgerock.doc.maven.pre;

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

/**
 * Use <a href="http://plantuml.sourceforge.net/">PlantUML</a> to generate images.
 *
 * <p>
 *
 * This class expects .txt files in the DocBook XML sources
 * that contain PlantUML diagrams.
 *
 * <p>
 *
 * It transforms the files to images in the same directories as the files.
 */
public class PlantUml {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public PlantUml(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Run PlantUML on .txt files in the DocBook source files.
     *
     * @throws MojoExecutionException Failed to run PlantUML.
     */
    public void execute() throws MojoExecutionException {

        // JCite to a temporary directory...
        Executor exec = new Executor();
        exec.runPlantUml();
    }

    /**
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Run PlantUML on .txt files in the DocBook source files.
         *
         * @throws MojoExecutionException Failed to run PlantUml.
         */
        void runPlantUml() throws MojoExecutionException {

            final String directory = m.path(m.getDocbkxModifiableSourcesDirectory());

            executeMojo(
                    plugin(
                            groupId("com.github.jeluard"),
                            artifactId("plantuml-maven-plugin"),
                            version("1.0"),
                            dependencies(
                                    dependency(
                                            groupId("net.sourceforge.plantuml"),
                                            artifactId("plantuml"),
                                            version(m.getPlantUmlVersion())))),
                    goal("generate"),
                    configuration(
                            element(name("sourceFiles"),
                                    element(name("directory"), directory),
                                    element(name("includes"),
                                            element(name("include"), "**/*.txt"))),
                            element(name("outputInSourceDirectory"), "true"),
                            element(name("verbose"), "true")),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }
    }
}
