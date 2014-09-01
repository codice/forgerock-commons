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

package org.forgerock.doc.maven.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Copy SyntaxHighlighter files under the specified directories.
 */
public final class SyntaxHighlighterCopier {

    /**
     * SyntaxHighlighter CSS files.
     */
    private final String[] shCssResources = {
        "/css/shCore.css",
        "/css/shCoreEclipse.css",
        "/css/shThemeEclipse.css"
    };

    /**
     * SyntaxHighlighter JavaScript files.
     */
    private final String[] shJavaScriptResources = {
        "/js/shCore.js",
        "/js/shBrushAci.js",
        "/js/shBrushBash.js",
        "/js/shBrushCsv.js",
        "/js/shBrushHttp.js",
        "/js/shBrushJava.js",
        "/js/shBrushJScript.js",
        "/js/shBrushLDIF.js",
        "/js/shBrushPlain.js",
        "/js/shBrushProperties.js",
        "/js/shBrushXml.js",
        "/js/shAll.js"
    };

    /**
     * Directories where SyntaxHighlighter scripts and CSS are to be added.
     */
    private String[] outputDirectories;

    /**
     * Construct a SyntaxHighlighterCopier, specifying output directories.
     *
     * @param outputDirectories Full path to directories under which to copy files.
     */
    public SyntaxHighlighterCopier(final String[] outputDirectories) {
        this.outputDirectories = outputDirectories;
    }

    /**
     * For each outputDirectory, copy SyntaxHighlighter files under outputDirectory/sh.
     *
     * @throws IOException Failed to copy files.
     */
    public void copy() throws IOException {
        addShCss();
        addShScripts();
    }

    /**
     * Add SyntaxHighlighter CSS files in each output directory.
     *
     * @throws IOException Failed to add CSS.
     */
    private void addShCss() throws IOException {
        addShResources(shCssResources);
    }

    /**
     * Add SyntaxHighlighter JavaScript files in each output directory.
     *
     * @throws IOException Failed to add scripts.
     */
    private void addShScripts() throws IOException {
        addShResources(shJavaScriptResources);
    }

    /**
     * Add SyntaxHighlighter resource files in each output directory.
     *
     * @param resources List of resource files to copy.
     * @throws IOException Failed to files.
     */
    private void addShResources(final String[] resources) throws IOException {

        for (String resource : resources) {
            URL resourceUrl = getClass().getResource(resource);

            // The html.stylesheet parameter should probably take URLs.
            // When local files are referenced,
            // the DocBook XSL stylesheets do not copy the files.
            // Instead the files must be copied to the output directories.

            for (final String outputDirectory : outputDirectories) {
                final File parent = new File(outputDirectory, "sh");
                final File styleSheetFile = new File(parent, resource);
                FileUtils.copyURLToFile(resourceUrl, styleSheetFile);
            }
        }
    }
}
