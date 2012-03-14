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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * Utility methods to prepare built HTML docs for publication.
 */
public final class HTMLUtils {
  /**
   * Add a <code>.htaccess</code> file to the base directory, for publication on
   * an Apache HTTPD server.
   * <p>
   * According to Apache documentation on <a
   * href="http://httpd.apache.org/docs/2.4/howto/htaccess.html#how">How
   * directives are applied</a>, "The configuration directives found in a
   * .htaccess file are applied to the directory in which the .htaccess file is
   * found, and to all subdirectories thereof." So there is no need to copy the
   * file recursively to all directories.
   *
   * @param baseDir
   *          Base directory under which to add the file
   * @param htaccess
   *          <code>.htaccess</code> file to copy
   * @throws IOException
   *           Something went wrong during copy procedure.
   */
  public static void addHtaccess(final String baseDir, final File htaccess)
      throws IOException {
    FileUtils.copyFileToDirectory(htaccess, new File(baseDir));
  }

  /**
   * Add <code>css/coredoc.css</code> in each subdirectory containing document
   * files. The entry point is typically <code>index.html</code>, though it can
   * be different. You pass the <code>coredoc.css</code> file. This method adds
   * the <code>css/</code> subdirectory.
   *
   * @param baseDir
   *          Base directory under which to find subdirectories
   * @param cssFile
   *          <code>coredoc.css</code> file to copy
   * @param entryPoint
   *          Entry point file for a document, next to which to copy
   *          <code>css/coredoc.css</code>
   * @return Directories to which <code>css/coredoc.css</code> was copied
   * @throws IOException
   *           Something went wrong during copy procedure.
   */
  public static List<File> addCss(final String baseDir, final File cssFile,
      final String entryPoint) throws IOException {
    FilenameFilter filter = new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(entryPoint);
      }
    };

    FilteredRecursiveFileCopier frfc = new FilteredRecursiveFileCopier(cssFile,
        filter, "css");
    return frfc.add(new File(baseDir));
  }

  /**
   * Replace an HTML tag with content from a template file.
   *
   * @param baseDir
   *          Base directory under which to find HTML files recursively
   * @param replacements
   *          Keys are tags to replace. Values are replacements, including the
   *          original tag.
   * @return List of files updated
   * @throws IOException
   *           Something went wrong reading or writing files.
   */
  static List<File> updateHTML(final String baseDir,
      final Map<String, String> replacements) throws IOException {
    // Match normal directories, and HTML files.
    IOFileFilter dirFilter = FileFilterUtils.and(
        FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
    IOFileFilter fileFilter = FileFilterUtils.and(
        FileFilterUtils.fileFileFilter(),
        FileFilterUtils.suffixFileFilter(".html"));
    FileFilter filter = FileFilterUtils.or(dirFilter, fileFilter);

    FilteredFileUpdater ffu = new FilteredFileUpdater(replacements, filter);
    return ffu.update(new File(baseDir));
  }

  /**
   * Not used.
   */
  private HTMLUtils() {
  }
}
