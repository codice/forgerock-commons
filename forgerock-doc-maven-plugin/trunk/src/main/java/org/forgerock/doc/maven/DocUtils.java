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
import java.util.Set;
import java.util.TreeSet;



/**
 * Utility methods to work with documents
 */
public class DocUtils
{
  /**
   * Rename document to reflect project and document name. For example,
   * index.pdf could be renamed OpenAM-Admin-Guide.pdf.
   *
   * @param projectName
   *          Short name of the project, such as OpenAM, OpenDJ, OpenIDM
   * @param docName
   *          Short name for the document, such as admin-guide, release-notes,
   *          reference
   * @param extension
   *          File name extension not including dot, e.g. pdf
   * @return New name for document. Can be "" if rename failed.
   */
  public static String renameDoc(String projectName, String docName,
      String extension)
  {
    return renameDoc(projectName, docName, "", extension);
  }



  /**
   * Rename document to reflect project and document name. For example,
   * index.pdf could be renamed OpenAM-10.0.0-Admin-Guide.pdf.
   *
   * @param projectName
   *          Short name of the project, such as OpenAM, OpenDJ, OpenIDM
   * @param docName
   *          Short name for the document, such as admin-guide, release-notes,
   *          reference
   * @param version
   *          Document version such as 10.0.0, 2.5.0, 2.0.2
   * @param extension
   *          File name extension not including dot, e.g. pdf
   * @return New name for document. Can be "" if rename failed.
   */
  public static String renameDoc(String projectName, String docName,
      String version, String extension)
  {
    if (isDocNameOk(docName))
    {
      docName = capitalize(docName);
    }
    else
    {
      return "";
    }

    // Need trailing dash when version is not empty.
    if (!version.equalsIgnoreCase(""))
    {
      version = version + '-';
    }

    // Only add a . if the extension is not empty.
    if (!extension.equalsIgnoreCase(""))
    {
      extension = "." + extension;
    }

    return projectName + '-' + version + docName + extension;
  }



  /**
   * Capitalize initial letters in a document name.
   *
   * @param docName
   *          Name of the document such as reference or admin-guide
   * @return Capitalized name such as Reference or Admin-Guide
   */
  private static String capitalize(String docName)
  {
    char[] chars = docName.toLowerCase().toCharArray();

    boolean isInitial = true;
    for (int i = 0; i < chars.length; i++)
    {
      if (isInitial && Character.isLetter(chars[i]))
      {
        chars[i] = Character.toUpperCase(chars[i]);
        isInitial = false;
      }
      else if (Character.isLetter(chars[i]))
      {
        isInitial = false;
      }
      else
      {
        isInitial = true;
      }
    }

    return String.valueOf(chars);
  }



  /**
   * Check that the document name contains only letters and dashes.
   *
   * @return True as long as the document name contains nothing else.
   */
  private static boolean isDocNameOk(String docName)
  {
    char[] chars = docName.toCharArray();

    for (char c : chars)
    {
      if (!(Character.isLetter(c) || c == '-')) return false;
    }

    return true;
  }



  /**
   * Returns names of directories that mirror the document names and contain
   * DocBook XML documents to build.
   *
   * @param srcDir
   *          Directory containing DocBook XML sources. Document directories
   *          like admin-guide or reference are one level below this directory.
   * @param docFile
   *          Name of a file common to all documents to build, such as
   *          index.xml.
   * @return Document names, as in admin-guide or reference
   */
  public static Set<String> getDocumentNames(File srcDir,
      final String docFile)
  {
    Set<String> documentDirectories = new TreeSet<String>();

    // Match directories containing DocBook document entry point files,
    // and ignore everything else.
    FileFilter filter = new FileFilter()
    {
      @Override
      public boolean accept(File file)
      {
        return file.isDirectory();
      }
    };

    File[] directories = srcDir.listFiles(filter);
    if (directories.length > 0)
    {

      FilenameFilter nameFilter = new FilenameFilter()
      {
        @Override
        public boolean accept(File file, String name)
        {
          return name.equalsIgnoreCase(docFile);
        }
      };

      for (File dir : directories)
      {
        String found[] = dir.list(nameFilter);
        if (found.length > 0)
        {
          documentDirectories.add(dir.getName());
        }
      }
    }

    return documentDirectories;
  }
}
