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
public class FilteredRecursiveFileCopier extends
    DirectoryWalker<File>
{
  /**
   * The file to copy
   */
  private final File fileToCopy;

  /**
   * The filter to match
   */
  private final FilenameFilter filterToMatch;

  /**
   * The parent subdirectory into which to copy the file
   */
  private final String parentToAdd;



  /**
   * {@inheritDoc}
   *
   * @param fileToCopy
   *          File to copy
   * @param filterToMatch
   *          Filter to match before adding the file to copy
   * @param parentToAdd
   *          Name of the parent subdirectory to enclose the file to copy
   */
  public FilteredRecursiveFileCopier(File fileToCopy,
      FilenameFilter filterToMatch, String parentToAdd)
  {
    super();
    this.fileToCopy = fileToCopy;
    this.filterToMatch = filterToMatch;
    this.parentToAdd = parentToAdd;
  }



  /**
   * Copy a file recursively into a subdirectory of all matching subdirectories.
   *
   * @param startDirectory
   *          Base directory under which to copy the file recursively
   * @return List of directories to which the file was copied
   * @throws IOException
   */
  public List<File> add(File startDirectory) throws IOException
  {
    List<File> results = new ArrayList<File>();
    walk(startDirectory, results);
    return results;
  }



  /**
   * Copy a file to the directory if the filter matches, adding the parent
   * subdirectory.
   *
   * @param directory
   *          Directory to which to copy the file
   * @param depth
   *          Not used
   * @param results
   *          List of directories to which files have been copied
   * @return True on success
   */
  @Override
  protected boolean handleDirectory(File directory, int depth,
      Collection<File> results) throws IOException
  {
    String[] matches = directory.list(filterToMatch);
    if (matches.length > 0)
    {
      File parent = new File(directory, parentToAdd);
      if (parent.mkdir())
      {
        FileUtils.copyFileToDirectory(fileToCopy, parent);
        results.add(directory);
      }
    }

    return true;
  }
}
