# ForgeRock Doc Build Maven Plugin

This Maven plugin centralizes configuration of core documentation, to ensure
that output documents are formatted uniformly.

With centralized configuration handled by this Maven plugin, the core
documentation-related project configuration takes at least two arguments:

*   `<projectName>`: the short name for the project such as OpenAM, OpenDJ,
    or OpenIDM
*   `<googleAnalyticsId>`: to add Google Analytics JavaScript to the HTML
    output

The project then runs two plugin executions:

1.  A `build` goal in the `pre-site` phase to build and massage output
2.  A `layout` goal in the `site` phase to copy content under
    `site-doc`

## Example Plugin Specification

You call the plugin from your `pom.xml` as follows. This example uses a
POM property called `gaId`, whose value is the Google Analytics ID.

		<build>
		 <plugins>
		  <plugin>
		   <groupId>org.forgerock.commons</groupId>
		   <artifactId>forgerock-doc-maven-plugin</artifactId>
		   <version>1.1.0-SNAPSHOT</version>
		   <inherited>false</inherited>
		   <configuration>
		    <projectName>MyProject</projectName>
		    <googleAnalyticsId>${gaId}</googleAnalyticsId>
		   </configuration>
		   <executions>
		    <execution>
		     <id>build-doc</id>
		     <phase>pre-site</phase>
		     <goals>
		      <goal>build</goal>
		     </goals>
		    </execution>
		    <execution>
		     <id>layout-doc</id>
		     <phase>site</phase>
		     <goals>
		      <goal>layout</goal>
		     </goals>
		    </execution>
		   </executions>
		  </plugin>
		 </plugins>
		</build>

## Source Layout Requirements

The assumption is that all of your DocBook XML documents are found under
`src/main/docbkx/` relative to the `pom.xml` file in which you call the
plugin. Documents are expected to be found in folders under that path, where
the folder name is a lowercase version of the document name, such as
release-notes, install-guide, admin-guide, reference, or similar. Furthermore,
all documents have the same file name for the file containing the top-level
document element, by default `index.xml`. The plugin expects to find all
images in an `images` folder inside the document folder.

An example project layout looks like this:

     src/main/docbkx/
      dev-guide/
       images/
       index.xml
       ...other files...
      install-guide/
       images/
       index.xml
       ...other files...
      reference/
       images/
       index.xml
       ...other files...
      release-notes/
       images/
       index.xml
       ...other files...
      shared/
       ...other files...

## Link Checking

By default, the plugin checks links found in the DocBook XML source, including
Olinks. You can find errors in the `target/linktester.err` file.

This capability is provided by Peter Major's
[linktester](https://github.com/aldaris/docbook-linktester) plugin.

## Excluding Output Formats

To exclude formats from the build, you can use the optional
`<excludes>` configuration element. The following example
excludes all formats but HTML from the build.

     <excludes>
      <exclude>epub</exclude>
      <exclude>man</exclude>
      <exclude>pdf</exclude>
      <exclude>rtf</exclude>
     </excludes>

## Generating Single-Chapter Output

By default, the plugin generates output for each document whose root is named
`index.xml`. You can change this by setting `documentSrcName` when you run
Maven. For example, if you want to produce pre-site output only for a chapter
named `chap-one.xml`, then you would set `documentSrcName` as follows.

    mvn -DdocumentSrcName=chap-one.xml clean pre-site

If you want only one type of output, then specify that using `include`.
The following command generates only PDF output for your single chapter.

    mvn -DdocumentSrcName=chap-one.xml -Dinclude=pdf clean pre-site

Formats include `epub`, `html`, `man`, `pdf`, and `rtf`.

## Expected Results

When you run the plugin with `mvn pre-site`, it builds the output formats,
which you find under `target/docbkx`. The plugin also runs the link check.

When you run the plugin with `mvn site`, it takes what was constructed during
the `pre-site` phase and moves it under `target/site/doc` as expected for a
Maven project site. The plugin adds an `index.html` in that directory that
redirects to `http://project.forgerock.org/docs.html`, so you do need one of
those in your Maven site.

The plugin also adds a `.htaccess` file under `target/site/doc` indicating to
Apache HTTPD server to compress text files like HTML and CSS.

## Release Layout

You can call the `release` goal in the site phase to prepare a doc layout
for release on docs.forgerock.org. When you call the release goal, be sure to
turn off draft mode, add a release version, and override the Google Analytics
ID using the property.

     mvn -DisDraftMode=no -DreleaseVersion=1.0.0 -D"gaId=UA-23412190-14" \
     -D"releaseDate=Software release date: January 1, 1970" \
     clean site org.forgerock.commons:forgerock-doc-maven-plugin:release

## Notes on Syntax Highlighting

Uses [SyntaxHighlighter](http://alexgorbatchev.com/SyntaxHighlighter/) 3.0.83,
rather than DocBook's syntax highlighting capabilities for HTML output, as
SyntaxHighlighter includes handy features for selecting and numbering lines
in HTML.

	 Source			SyntaxHighlighter	Brush Name
	 ---			---					---
	 aci			aci					shBrushAci.js
	 csv			csv					shBrushCsv.js
	 html			html				shBrushXml.js
	 http			http				shBrushHttp.js
	 ini			ini					shBrushProperties.js
	 java			java				shBrushJava.js
	 javascript		javascript			shBrushJScript.js
	 ldif			ldif				shBrushLDIF.js
	 none			plain				shBrushPlain.js
	 shell			shell				shBrushBash.js
	 xml			xml					shBrushXml.js

Brush support for `aci`, `csv`, `http`, `ini`, and `ldif` is provided by
[a fork of SyntaxHighlighter](https://github.com/markcraig/SyntaxHighlighter).

* * *
This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2012 ForgeRock AS
