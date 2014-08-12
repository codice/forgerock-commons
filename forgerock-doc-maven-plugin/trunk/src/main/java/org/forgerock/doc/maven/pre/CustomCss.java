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

package org.forgerock.doc.maven.pre;

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.AbstractDocbkxMojo;
import org.forgerock.doc.maven.utils.HtmlUtils;

import java.io.IOException;

/**
 * Add custom CSS for the normal, non-release build.
 */
public class CustomCss {

    /**
     * The Mojo that holds configuration and related methods.
     */
    private AbstractDocbkxMojo m;

    /**
     * Constructor setting the Mojo that holds the configuration.
     *
     * @param mojo The Mojo that holds the configuration.
     */
    public CustomCss(final AbstractDocbkxMojo mojo) {
        m = mojo;
    }

    /**
     * Add custom CSS to the modifiable copy of DocBook XML sources.
     *
     * @throws MojoExecutionException Failed to add CSS.
     */
    public void execute() throws MojoExecutionException {
        try {
            HtmlUtils.addCustomCss(
                    m.getPreSiteCss(),
                    m.getDocbkxModifiableSourcesDirectory(),
                    m.getDocumentSrcName());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
