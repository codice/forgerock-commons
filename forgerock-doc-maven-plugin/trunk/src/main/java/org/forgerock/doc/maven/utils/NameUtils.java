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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Utility methods to work with documents.
 */
public final class NameUtils {
    /**
     * Pattern to validate the document names.
     *
     * <p>Valid names:</p>
     * <ul>
     *     <li>guide</li>
     *     <li>admin-quide</li>
     *     <li>OpenTEST-guide</li>
     *     <li>OpenTEST-admin-guide</li>
     *     <li>OpenTEST-admin-guide-1.1.1.0</li>
     *     <li>OpenTEST-admin-guide-1.1.1.0-SNAPSHOT</li>
     *     <li>OpenTEST-admin-guide-1.1.1.0-express</li>
     *     <li>OpenTEST-admin-guide-1.1.1.0-Xpress</li>
     *     <li>OpenTEST-admin-guide-1.1.1.0-Xpress1</li>
     *     <li>OpenTEST-10.1.0-admin-guide</li>
     *     <li>OpenTEST-10.1.0-SNAPSHOT-admin-guide</li>
     *     <li>OpenTEST-10.1.0-Xpress2-admin-guide</li>
     *     <li>db2-connector-1.1.0.0-SNAPSHOT</li>
     * </ul>
     *
     * <p>Invalid names:</p>
     * <ul>
     *     <li>guide.</li>
     *     <li>guide-1</li>
     *     <li>guide-.</li>
     * </ul>
     */
    public static final Pattern DOCUMENT_FILE_PATTERN = Pattern
            .compile("^([a-zA-Z0-9]+)(-?[0-9].[0-9\\.]*[0-9])?(-SNAPSHOT|(-Ex|-ex|-X)press[0-9])"
                    + "?([a-zA-Z-]*)((-?[0-9].[0-9\\.]*[0-9])?-?(SNAPSHOT|(Ex|ex|X)press[0-9]?)?)$");

    /**
     * Pattern to find version sting.
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("(-[0-9].[0-9.]*[0-9])");

    /**
     * Rename document to reflect project and document name. For example,
     * index.pdf could be renamed OpenAM-Admin-Guide.pdf.
     *
     * @param projectName
     *            Short name of the project, such as OpenAM, OpenDJ, OpenIDM
     * @param docName
     *            Short name for the document, such as admin-guide,
     *            release-notes, reference
     * @param extension
     *            File name extension not including dot, e.g. pdf
     * @return New name for document. Can be "" if rename failed.
     */
    public static String renameDoc(final String projectName,
            final String docName, final String extension) {
        return renameDoc(projectName, docName, "", extension);
    }

    /**
     * Rename document to reflect project and document name. For example,
     * index.pdf could be renamed OpenAM-10.0.0-Admin-Guide.pdf.
     *
     * @param projectName
     *            Short name of the project, such as OpenAM, OpenDJ, OpenIDM
     * @param docName
     *            Short name for the document, such as admin-guide,
     *            release-notes, reference
     * @param version
     *            Document version such as 10.0.0, 2.5.0, 2.0.2
     * @param extension
     *            File name extension not including dot, e.g. pdf
     * @return New name for document. Can be "" if rename failed.
     */
    public static String renameDoc(final String projectName,
            final String docName, final String version, final String extension) {
        String doc = docName;

        Matcher docNameMatcher = DOCUMENT_FILE_PATTERN.matcher(docName);

        if (docNameMatcher.matches()) {
            doc = capitalize(doc);
        } else {
            return "";
        }

        StringBuilder sb = StringUtils.isNotBlank(projectName)
                           ? (new StringBuilder(projectName)).append("-")
                           : new StringBuilder();

        // Only add a . if the extension is not empty.
        String ext = extension;
        if (!ext.equalsIgnoreCase("")) {
            ext = "." + ext;
        }

        Matcher versionMatcher = VERSION_PATTERN.matcher(docName);
        if (!versionMatcher.matches() && StringUtils.isNotBlank(version)) {
            sb.append(version).append("-");
        }
        return sb.append(doc).append(ext).toString();
    }

    /**
     * Capitalize initial letters in a document name.
     *
     * @param docName
     *            Name of the document such as reference or admin-guide
     * @return Capitalized name such as Reference or Admin-Guide
     */
    private static String capitalize(final String docName) {
        char[] chars = docName.toLowerCase().toCharArray();

        boolean isInitial = true;
        for (int i = 0; i < chars.length; i++) {
            if (isInitial && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                isInitial = false;
            } else {
                isInitial = !Character.isLetter(chars[i]);
            }
        }

        return String.valueOf(chars);
    }

    /**
     * Returns names of directories that mirror the document names and contain
     * DocBook XML documents to build.
     *
     * @param srcDir
     *            Directory containing DocBook XML sources. Document directories
     *            like admin-guide or reference are one level below this
     *            directory.
     * @param docFile
     *            Name of a file common to all documents to build, such as
     *            index.xml.
     * @return Document names, as in admin-guide or reference
     */
    public static Set<String> getDocumentNames(final File srcDir,
            final String docFile) {
        Set<String> documentDirectories = new TreeSet<String>();

        // Match directories containing DocBook document entry point files,
        // and ignore everything else.
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isDirectory();
            }
        };

        File[] directories = srcDir.listFiles(filter);
        if (directories != null && directories.length > 0) {

            FilenameFilter nameFilter = new FilenameFilter() {
                @Override
                public boolean accept(final File file, final String name) {
                    return name.equalsIgnoreCase(docFile);
                }
            };

            for (File dir : directories) {
                String[] found = dir.list(nameFilter);
                if (found.length > 0) {
                    documentDirectories.add(dir.getName());
                }
            }
        }

        return documentDirectories;
    }

    /**
     * Rename a single built document.
     * For example, rename {@code index.pdf} to {@code OpenAM-Admin-Guide.pdf}.
     *
     * @param builtDocument File to rename, such as {@code index.pdf}.
     * @param docName       Simple document name such as {@code admin-guide}.
     * @param projectName   Project name, such as {@code OpenAM}.
     * @throws MojoExecutionException Something went wrong renaming the file.
     */
    public static void renameDocument(final File builtDocument,
                                            final String docName,
                                            final String projectName)
            throws MojoExecutionException {

        String ext = FilenameUtils.getExtension(builtDocument.getName());
        String newName = builtDocument.getParent() + File.separator
                + renameDoc(projectName, docName, ext);
        try {
            File newFile = new File(newName);
            if (!newFile.exists()) {
                FileUtils.moveFile(builtDocument, newFile);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to rename " + builtDocument);
        }
    }

    /**
     * Not used.
     */
    private NameUtils() {
    }
}
