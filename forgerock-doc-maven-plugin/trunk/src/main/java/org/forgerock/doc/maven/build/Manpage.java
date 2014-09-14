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

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.util.ArrayList;

/**
 * Build man page output.
 */
public class Manpage {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public Manpage(final AbstractDocbkxMojo mojo) {
        m = mojo;
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
     * Enclose methods to run plugins.
     */
    class Executor extends MojoExecutor {

        /**
         * Prepare olink target database from DocBook XML sources.
         *
         * @throws MojoExecutionException Failed to build target database.
         */
        void prepareOlinkDB() throws MojoExecutionException {
            // Not implemented yet.
        }

        /**
         * Build documents from DocBook XML sources.
         *
         * @throws MojoExecutionException Failed to build the output.
         */
        void build() throws MojoExecutionException {
            ArrayList<Element> cfg = new ArrayList<MojoExecutor.Element>();
            cfg.addAll(m.getBaseConfiguration());
            cfg.add(element(name("includes"), "*/" + m.getDocumentSrcName()));
            cfg.add(element(name("manpagesCustomization"), m.path(m.getManpagesCustomization())));
            cfg.add(element(name("targetDirectory"), m.path(m.getDocbkxOutputDirectory()) + "/manpages"));

            executeMojo(
                    plugin(
                            groupId("com.agilejava.docbkx"),
                            artifactId("docbkx-maven-plugin"),
                            version(m.getDocbkxVersion())),
                    goal("generate-manpages"),
                    configuration(cfg.toArray(new Element[cfg.size()])),
                    executionEnvironment(m.getProject(), m.getSession(), m.getPluginManager()));
        }
    }
}
