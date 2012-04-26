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
 *     Copyright 2012 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;

/**
 * Copy a file to all directories holding matching files, creating a parent
 * directory into which to copy the file.
 */
public class FilteredRecursiveFileCopier extends DirectoryWalker<File> {
    /**
     * The file to copy.
     */
    private final File fFileToCopy;

    /**
     * The filter to match.
     */
    private final FilenameFilter ffFilterToMatch;

    /**
     * The parent subdirectory into which to copy the file.
     */
    private final String sParentToAdd;

    /**
     * Copier that adds a subdirectory containing a file into all directories.
     *
     * @param fileToCopy
     *            File to copy
     * @param filterToMatch
     *            Filter to match before adding the file to copy
     * @param parentToAdd
     *            Name of the parent subdirectory to enclose the file to copy
     */
    public FilteredRecursiveFileCopier(final File fileToCopy,
            final FilenameFilter filterToMatch, final String parentToAdd) {
        super();
        this.fFileToCopy = fileToCopy;
        this.ffFilterToMatch = filterToMatch;
        this.sParentToAdd = parentToAdd;
    }

    /**
     * Copy a file recursively into a subdirectory of all matching directories.
     *
     * @param startDirectory
     *            Base directory under which to copy the file recursively
     * @return List of directories to which the file was copied
     * @throws IOException
     *             Something went wrong copying the file.
     */
    public final List<File> add(final File startDirectory) throws IOException {
        List<File> results = new ArrayList<File>();
        walk(startDirectory, results);
        return results;
    }

    /**
     * Copy a file to the directory if the filter matches, adding the parent
     * subdirectory.
     *
     * @param directory
     *            Directory to which to copy the file
     * @param depth
     *            Not used
     * @param results
     *            List of directories to which files have been copied
     * @return True on success
     * @throws IOException
     *             Something went wrong copying the file.
     */
    @Override
    protected final boolean handleDirectory(final File directory,
            final int depth, final Collection<File> results) throws IOException {
        String[] matches = directory.list(ffFilterToMatch);
        if (matches.length > 0) {
            File parent = new File(directory, sParentToAdd);
            if (parent.mkdir()) {
                FileUtils.copyFileToDirectory(fFileToCopy, parent);
                results.add(directory);
            }
        }

        return true;
    }
}
