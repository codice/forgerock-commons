# DRAFT IN PROGRESS

Latest release is 2.1.5. These are draft release notes.


# ForgeRock Documentation Tools 3.0.0 Release Notes

ForgeRock Documentation Tools is a catch all for
the doc Maven plugin,
default branding and common content,
sites where we post documentation,
and the documentation about documentation.

The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.


## Compatibility

**Changes to default output formats**

With the fix for DOCS-189, RTF is no longer a default format.
As a result, you should no doubt remove links to in-progress RTF documents.

**Changes to plugin execution goals**

The fix for DOCS-108 is a major refactoring of the doc Maven plugin.

As a result of the refactoring, the plugin now has these goals:

*   A `pre-site` phase `process` goal to pre-process documents
*   A `pre-site` phase `build` goal to generate output
*   A `site` phase `site` goal to copy documents to a site layout
*   A `site` phase `release` goal to copy documents to a release layout

All configuration elements can be used in the top-level plugin configuration,
rather than the per-execution configurations.

These changes mean that you must update your POM in order to use this version.
See the README for this version for details.

**Changes to plugin configuration**

The fix for DOCS-240 replaces configuration parameters
`<include>` and `<excludes>` with `<formats>`.

The fix for DOCS-239 eliminates configuration settings
not generally set by any projects using the plugin.

The fix for DOCS-237 introduces a new configuration parameter, `<skipUrlPatterns>`.

The fix for DOCS-216 introduces a new configuration parameter,
`<doCreateArtifacts>`, to build artifacts from pre-processed sources.
See the explanation below for details.

Due to changes in the plugin architecture,
the fix for DOCS-194 in this release does not provide
the configuration option `<overwriteGeneratedSource>`,
but instead provides similar capability through other settings.
See the explanation below for details.

The fix for DOCS-144 makes Bootstrap-styled HTML and PDF the default formats.
To build other formats, set the `formats` property as in `-Dformats=xhtml5`.

**Changes to source code licensing**

ForgeRock doc build tools have moved to CDDL to align with other ForgeRock projects.


## What's New

**DOCS-264: Refine PDF naming for release documentation**

The fix allows the plugin to handle project names that include spaces.

**DOCS-261: Allow building output formats from pre-processed DocBook XML 5**

If the DocBook XML sources have already been fully pre-processed,
set `<usePreProcessedSources>true</usePreProcessedSources>`
(or `-DusePreProcessedSources=true`)
and set `<docbkxSourceDirectory>` (or `-DdocbkxSourceDirectory`)
to the file system directory containing the pre-processed sources.

**DOCS-239: Reduce the list of configuration settings**

This change removes these configuration settings, relying instead on the default values:

*   `<ansi>`: `true`
*   `<areSectionsAutolabeled>`: `true`
*   `<chunkedHTMLCustomization>`: `${project.build.directory}/docbkx-stylesheets/html/chunked.xsl`
*   `<docbkxModifiableSourcesDirectory>`: `${project.build.directory}/docbkx-sources`
*   `<docbkxOutputDirectory>`: `${project.build.directory}/docbkx`
*   `<doesSectionLabelIncludeComponentLabel>`: `true`
*   `<epubCustomization>`: `${project.build.directory}/docbkx-stylesheets/epub/coredoc.xsl`
*   `<foCustomization>`: `${project.build.directory}/docbkx-stylesheets/fo/coredoc.xsl`
*   `<fontsDirectory>`: `${project.build.directory}/fonts`
*   `<isXincludeSupported>`: `true`
*   `<javaScriptFileName>`: `uses-jquery.js`
*   `<manpagesCustomization>`: `${project.build.directory}/docbkx-stylesheets/man/coredoc.xsl`
*   `<preSiteCssFileName>`: `${project.build.directory}/coredoc.css`
*   `<releaseCssFileName>`: `${project.build.directory}/dfo.css`
*   `<singleHTMLCustomization>`: `${project.build.directory}/docbkx-stylesheets/html/coredoc.xsl`
*   `<useSyntaxHighlighting>`: `1` (true)
*   `<webhelpCss>`: `${project.build.dir}/docbkx-stylesheets/webhelp/positioning.css`
*   `<webhelpCustomization>`: `${project.build.dir}/docbkx-stylesheets/webhelp/coredoc.xsl`
*   `<webhelpLogo>`: `${project.build.dir}/docbkx-stylesheets/webhelp/logo.png`
*   `<xhtml5Customization>`: `${project.build.directory}/docbkx-stylesheets/fo/coredoc.xsl`

This change effectively reserves `${project.build.directory}/docbkx*` for use by this plugin.
This plugin uses `${project.build.directory}/fonts`,
`${project.build.directory}/coredoc.css` and `${project.build.directory}/dfo.css`.

This change also constrains the stylesheet names and layouts
that custom branding artifacts can use.


**DOCS-237: Provide a configuration parameter for passing skipUrl regexes**

This introduces a `<skipUrlPatterns>` parameter
that mirrors the one provided by `docbook-linktester`.

**DOCS-216: Provide pre-processed sources as Maven artifacts**

Unless you set `<doCreateArtifacts>` to `false`,
the plugin builds a Maven artifact from pre-processed documentation sources.

The resulting artifact is named `artifactId-version-doc-sources.jar`,
where `artifactId` and `version` are those of your project,
and the classifier is `doc-sources`.

When building output directly from pre-processed sources,
use the Maven dependency plugin to retrieve and unpack the sources
before you call this plugin.

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-dependency-plugin</artifactId>
      <executions>
        <execution>
          <goals>
            <goal>unpack</goal>
          </goals>
          <phase>pre-site</phase>
          <configuration>
            <artifactItems>
              <artifactItem>
                <groupId>${myGroupId}</groupId>
                <artifactId>${myArtifactId}</artifactId>
                <version>${myVersion}</version>
                <classifier>doc-sources</classifier>
                <outputDirectory>${project.build.directory}/db-src</outputDirectory>
              </artifactItem>
            </artifactItems>
          </configuration>
        </execution>
      </executions>
    </plugin>

Skip the `process` goal in the configuration for this plugin,
and instead specify `<docbkxSourceDirectory>` to pick up the pre-processed files.

**DOCS-235: Add PI for table header row color in PDF**

This makes the table header row background color light gray in PDF.

**DOCS-215: Add configuration for stopping at pre-processed DocBook**

Superseded by DOCS-216.

**DOCS-203: Provide a convention for copying arbitrary files to the built documentation**

The fix introduces two settings, `<copyResourceFiles>` (default: `false`),
and `<resourcesDirectory>` (default: `src/main/docbkx/resources`).

See the README for additional information. 


**DOCS-198: Build a cleaner version of the HTML**

You can now build XHTML5 that applies only default styles,
plus syntax highlighting.

XHTML5 is not built automatically. Specify the format as an include.

    mvn -Dformats=xhtml5 clean pre-site

The resulting documents are single pages, not chunked.


**DOCS-194:  Add option to allow merge of generated and docbkx source directories**

This release introduces a boolean option,
`<overwriteProjectFilesWithSharedContent>` (Default: `true`),
that allows you to prevent shared content from being overwritten
with shared content from the common content artifact.
To avoid overwriting existing files with shared content,
set the option to `false`.

One of the first things the plugin does when preparing DocBook sources
is to make a working copy of the files that is separate from the sources
in the `<docbkxModifiableSourcesDirectory>`.
This allows the plugin to make changes to the files as necessary.

If for some reason you must provide the copy yourself,
and your copy must be in the `<docbkxModifiableSourcesDirectory>`,
then to prevent the plugin from replacing the copy,
set `<overwriteModifiableCopy>` to `false`.
The plugin with then pre-process the copy, however,
so expect the files in your modifiable copy to be changed.


**DOCS-187: Documentation for legacy versions should not have the report bug footer**

The fix for this relies on a list of EOSL versions.
If the current version matches a version in the EOSL list,
then the footer is not shown in the HTML pages.

For the full list of EOSL versions, see the
[EOSL page at support.forgerock.com](https://support.forgerock.com/entries/24898647-Product-release-and-EOSL-dates).


**DOCS-178: Apply Maven resource filtering to .txt files as well**

The fix for this issue addresses both .txt and also .json files.


**DOCS-144: Style HTML output with Bootstrap UI**

The fix for this introduces a `bootstrap` format
that generates each document as a single HTML page styled with Bootstrap
and including a left-menu TOC on screens of appropriate size.

This fix also sets the default formats to the equivalent of `-Dformats=bootstrap,pdf`.


**DOCS-108: Reconsider forgerock-doc-maven-plugin architecture**

The fix for this issue is a major refactoring of the doc Maven plugin,
and a simplification of the plugin configuration model.

The new architecture is described in the Design document.

**DOCS-119: Support Maven 3.1 & 3.2**

The plugin has been tested with Maven 3.0.5, 3.1.1, 3.2.3.


## Fixes

**DOCS-244: Webhelp format does not include noindex,nofollow meta tag*

**DOCS-240: `<include>` (singular) and `<excludes>` are confusing**

This fix replaces those settings with `<formats>`.

**DOCS-233: Build plugin fails processing child module site**

**DOCS-225: Run docbook-linktester only on pre-processed DocBook files**

**DOCS-220: Chapter numbers should be included in WebHelp format**

**DOCS-213: Linktester phase in doc build plugin fails to detect broken links**

**DOCS-197: Prepending ↪ on mouseover is distracting**

**DOCS-189: Discontinue default support for RTF**

**DOCS-188: JavaScript icons in HTML to (un)fold screen are not accessible**

The fix removes the icons and (un)fold functionality.

**DOCS-184: Remove automatic line-breaking at the '?' character**

The fix only breaks URL lines longer than 80 characters in PDF.

**DOCS-179: Issue with Maven HTML builds and images**

**DOCS-150: When a code sample is unwrapped, it it not limited to the width of the page**

The fix for DOCS-188 removed the functionality that was causing the problem.

**DOCS-59: Only the draft documents are optimized to appear in Google searches; the final docs need to be SEO too.**

The fix adds a robots meta tag (noindex, nofollow) to site HTML,
that it then removes from release HTML.


## Known Issues

**DOCS-163: The performance="optional" attr in a step has no effect**

**DOCS-132: Soft hyphens used to break lines are rendered in PDF as hyphen + space**

Although soft hyphens are not used in this release,
the line break for hyphenation still remains.

See <https://issues.apache.org/jira/browse/FOP-2358>.

Workaround: Fix the content after copy/paste.


* * *

The contents of this file are subject to the terms of the Common Development and
Distribution License (the License). You may not use this file except in compliance with the
License.

You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
specific language governing permission and limitations under the License.

When distributing Covered Software, include this CDDL Header Notice in each file and include
the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
Header, with the fields enclosed by brackets [] replaced by your own identifying
information: "Portions copyright [year] [name of copyright owner]".

Copyright 2014-2015 ForgeRock AS.
