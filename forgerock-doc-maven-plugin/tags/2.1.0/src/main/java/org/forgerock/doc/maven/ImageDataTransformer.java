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

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Update XML files, transforming &lt;imagedata> elements to make sure they use
 * {@code scalefit="1"}, {@code width="100%"}, and {@code contentdepth="100%"}
 * attributes.
 */
public class ImageDataTransformer extends DirectoryWalker<File> {

    /**
     * Construct an updater to match DocBook XML files.
     *
     * <p>
     *
     * The files are updated in place.
     *
     * <p>
     * The following example shows how this might be used in your code with
     * tools from Apache Commons.
     * <pre>
     *     File xmlSourceDirectory = new File("/path/to/xml/files/");
     *
     *     // Match normal directories, and XML files.
     *     IOFileFilter dirFilter = FileFilterUtils
     *          .and(FileFilterUtils.directoryFileFilter(),
     *          HiddenFileFilter.VISIBLE);
     *     IOFileFilter fileFilter = FileFilterUtils.and(
     *          FileFilterUtils.fileFileFilter(),
     *          FileFilterUtils.suffixFileFilter(".xml"));
     *     FileFilter filterToMatch = FileFilterUtils.or(dirFilter, fileFilter);
     *
     *     // Update XML files.
     *     ImageDataTransformer idt = new ImageDataTransformer(filterToMatch);
     *     return idt.update(xmlSourceDirectory);
     * </pre>
     *
     * @param  filterToMatch Filter to match XML files.
     */
    public ImageDataTransformer(final FileFilter filterToMatch) {
        super(filterToMatch, -1);

        try {
            this.transformer = getTransformer();
        } catch (IOException ie) {
            System.err.println(ie.getStackTrace());
            System.exit(1);
        } catch (TransformerConfigurationException tce) {
            System.err.println(tce.getStackTrace());
            System.exit(1);
        }
    }

    private Transformer transformer;
    private final String imageDataXSLT = "/xslt/imagedata.xsl";
    private Transformer getTransformer() throws IOException, TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(getClass().getResource(imageDataXSLT).openStream());
        return factory.newTransformer(xslt);
    }

    /**
     * Update files that match the filter.
     *
     * @param startDirectory
     *            Base directory under which to update files, recursively
     * @return List of updated files
     * @throws java.io.IOException
     *             Something went wrong changing a file's content.
     */
    public final List<File> update(final File startDirectory) throws IOException {
        List<File> results = new ArrayList<File>();
        walk(startDirectory, results);
        return results;
    }

    /**
     * Update files that match, adding them to the results.
     *
     * @param file
     *            File to update
     * @param depth
     *            Not used
     * @param results
     *            List of files updated
     * @throws IOException
     *             Something went wrong changing a file's content.
     */
    @Override
    protected final void handleFile(final File file, final int depth, final Collection<File> results)
            throws IOException {
        if (file.isFile()) {
            try {
                Source xml = new StreamSource(file);
                File tmpFile = File.createTempFile(file.getName(), ".tmp");
                transformer.transform(xml, new StreamResult(tmpFile));

                FileUtils.deleteQuietly(file);
                FileUtils.moveFile(tmpFile, file);
                results.add(file);
            } catch (TransformerException te) {
                throw new IOException("Failed to transform " + file.getPath()
                        + ": " + te.getStackTrace());
            }
        }
    }
}
