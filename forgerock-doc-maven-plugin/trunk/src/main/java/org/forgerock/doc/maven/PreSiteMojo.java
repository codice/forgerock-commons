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

package org.forgerock.doc.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.forgerock.doc.maven.build.ChunkedHtml;
import org.forgerock.doc.maven.build.Epub;
import org.forgerock.doc.maven.build.Manpage;
import org.forgerock.doc.maven.build.Pdf;
import org.forgerock.doc.maven.build.Rtf;
import org.forgerock.doc.maven.build.SingleHtml;
import org.forgerock.doc.maven.build.Webhelp;
import org.forgerock.doc.maven.build.Xhtml5;
import org.forgerock.doc.maven.post.Html;
import org.forgerock.doc.maven.post.NoOp;
import org.forgerock.doc.maven.post.WebhelpPost;
import org.forgerock.doc.maven.post.Xhtml;
import org.forgerock.doc.maven.pre.Branding;
import org.forgerock.doc.maven.pre.CommonContent;
import org.forgerock.doc.maven.pre.CurrentDocId;
import org.forgerock.doc.maven.pre.CustomCss;
import org.forgerock.doc.maven.pre.Dpi;
import org.forgerock.doc.maven.pre.Filter;
import org.forgerock.doc.maven.pre.Fop;
import org.forgerock.doc.maven.pre.ImageData;
import org.forgerock.doc.maven.pre.JCite;
import org.forgerock.doc.maven.pre.ModifiableCopy;
import org.forgerock.doc.maven.pre.PlantUml;
import org.forgerock.doc.maven.pre.XCite;

import java.util.List;

/**
 * Call other classes to perform pre-site build.
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PRE_SITE)
public class PreSiteMojo extends AbstractDocbkxMojo {

    /**
     * Call other classes to perform pre-site build.
     *
     * @throws MojoExecutionException   Failed to build successfully.
     * @throws MojoFailureException     Failed to build successfully.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (!getBuildDirectory().exists()) {
            getBuildDirectory().mkdir();
        }
        if (!getBuildDirectory().isDirectory()) {
            throw new MojoExecutionException("No build directory available.");
        }

        final List<Format> formats = getFormats();

        // Perform pre-processing.
        new Branding(this).execute();
        new ModifiableCopy(this).execute();
        new CommonContent(this).execute();
        new JCite(this).execute();
        new XCite(this).execute();
        new Filter(this).execute();
        new ImageData(this).execute();
        new PlantUml(this).execute();

        if (formats.contains(Format.pdf) || formats.contains(Format.rtf)) {
            new Dpi(this).execute();
        }

        new CurrentDocId(this).execute();

        if (formats.contains(Format.pdf) || formats.contains(Format.rtf)) {
            new Fop(this).execute();
        }
        if (formats.contains(Format.html)) {
            new CustomCss(this).execute();
        }


        // When not producing final output, but only preprocessed XML,
        // we can interrupt processing now.
        if (stopAfterPreProcessing()) {
            getLog().info(
                    "Pre-processed sources are available under "
                            + getDocbkxModifiableSourcesDirectory().getPath());
            return;
        }


        // Perform build.
        if (formats.contains(Format.epub)) {
            new Epub(this).execute();
        }
        if (formats.contains(Format.html)) {
            new SingleHtml(this).execute();
            new ChunkedHtml(this).execute();
        }
        if (formats.contains(Format.man)) {
            new Manpage(this).execute();
        }
        if (formats.contains(Format.pdf)) {
            new Pdf(this).execute();
        }
        if (formats.contains(Format.rtf)) {
            new Rtf(this).execute();
        }
        if (formats.contains(Format.webhelp)) {
            new Webhelp(this).execute();
        }
        if (formats.contains(Format.xhtml5)) {
            new Xhtml5(this).execute();
        }

        // Perform post-processing.
        if (formats.contains(Format.epub)) {
            new NoOp(this).execute();
        }
        if (formats.contains(Format.html)) {
            new Html(this).execute();
        }
        if (formats.contains(Format.man)) {
            new NoOp(this).execute();
        }
        if (formats.contains(Format.pdf)) {
            new NoOp(this).execute();
        }
        if (formats.contains(Format.rtf)) {
            new NoOp(this).execute();
        }
        if (formats.contains(Format.webhelp)) {
            new WebhelpPost(this).execute();
        }
        if (formats.contains(Format.xhtml5)) {
            new Xhtml(this).execute();
        }
    }
}
