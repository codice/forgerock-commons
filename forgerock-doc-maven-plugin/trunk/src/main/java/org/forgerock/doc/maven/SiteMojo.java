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

package org.forgerock.doc.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.forgerock.doc.maven.site.Htaccess;
import org.forgerock.doc.maven.site.Layout;
import org.forgerock.doc.maven.site.LinkTest;
import org.forgerock.doc.maven.site.Redirect;

/**
 * Call other classes to copy docs to site build directory.
 *
 * @Checkstyle:ignoreFor 2
 * @goal site
 * @phase site
 */
public class SiteMojo extends AbstractDocbkxMojo {

    /**
     * Call other classes to copy docs to site build directory.
     *
     * @throws MojoExecutionException Failed to copy docs successfully.
     */
    @Override
    public void execute() throws MojoExecutionException {

        // When not producing final output, but only preprocessed XML,
        // we can interrupt processing now.
        if (stopAfterPreProcessing()) {
            return;
        }

        new Layout(this).execute();
        new Htaccess(this).execute();
        new Redirect(this).execute();
        new LinkTest(this).execute();
    }
}
