/* @Checkstyle:ignoreFor 17
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

/**
 * Provides the implementation for building ForgeRock core documentation from <a
 * href="http://www.docbook.org/tdg51/en/html/docbook.html">DocBook XML</a>
 * using <a href="http://code.google.com/p/docbkx-tools/">docbkx-tools</a>.
 * <p>
 * Top-level DocBook documents included in the documentation set such as
 * books, articles, and references share a common entry point, which is a file
 * having the name specified by this element.
 * <p>
 * For example, if your documentation set has Release Notes, an Installation
 * Guide, a Developer's Guide, and a Reference, your source layout under the
 * base DocBook XML source directory might look like this, assuming you use
 * the default file name, <code>index.xml</code>.
 *
 * <pre>
 * src/main/docbkx/
 *  dev-guide/
 *   index.xml
 *   ...other files...
 *  install-guide/
 *   index.xml
 *   ...other files...
 *  reference/
 *   index.xml
 *   ...other files...
 *  release-notes/
 *   index.xml
 *   ...other files...
 *  shared/
 *   ...other files...
 * </pre>
 *
 * The <code>...other files...</code> can have whatever names you want, as
 * long as the name does not conflict with the file name you set here.
 * <p>
 * See the plugin README.md for more information and example specifications
 * for adding the plugin to your Maven project.
 */
package org.forgerock.doc.maven;
