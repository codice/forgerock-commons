/*
 * MPL 2.0 HEADER START
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If applicable, add the following below this MPL 2.0 HEADER, replacing
 * the fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *     Portions Copyright [yyyy] [name of copyright owner]
 *
 * MPL 2.0 HEADER END
 *
 *     Copyright 2012 ForgeRock AS
 *
 */

package org.forgerock.doc.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;

/**
 * Copy a file to all directories.
 */
public class RecursiveFileCopier extends DirectoryWalker<File> {

  /**
   * The file to copy.
   */
  private final File fFileToCopy;

  /**
   * Copier to add a file recursively to all directories.
   *
   * @param fileToCopy
   *          File to copy
   */
  public RecursiveFileCopier(final File fileToCopy) {
    super();
    this.fFileToCopy = fileToCopy;
  }

  /**
   * Copy a file recursively to all directories.
   *
   * @param startDirectory
   *          Base directory under which to copy the file recursively
   * @return List of directories to which the file was copied
   * @throws IOException
   *           Something went wrong copying the file.
   */
  public final List<File> add(final File startDirectory) throws IOException {
    List<File> results = new ArrayList<File>();
    walk(startDirectory, results);
    return results;
  }

  /**
   * Copy a file to a directory.
   *
   * @param directory
   *          Directory to which to copy the file
   * @param depth
   *          Not used
   * @param results
   *          List of directories to which files have been copied
   * @return True on success
   * @throws IOException
   *           Something went wrong copying the file.
   */
  @Override
  protected final boolean handleDirectory(final File directory,
      final int depth, final Collection<File> results) throws IOException {
    FileUtils.copyFileToDirectory(fFileToCopy, directory);
    results.add(directory);
    return true;
  }
}
