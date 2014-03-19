# DRAFT - IN PROGRESS

# ForgeRock Documentation Tools 2.1.0 Release Notes

ForgeRock Documentation Tools is a catch all for the doc build plugin,
sites where we post documentation, and the documentation about
documentation. The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.

This release includes the following improvements, new features, and bug fixes.

## Improvements & New Features

**DOCS-7: Use make.clean.html and docbook.css.source for styling HTML output**

**DOCS-10: Keep overall TOC present as side menu in core documentation**

DocBook XSL webhelp output includes a (retractable) left menu TOC,
among other features, such as JavaScript for local search.
For more information on webhelp,
see <http://docbook.sourceforge.net/release/xsl/current/webhelp/docs/>.

The fix for this issue is to build webhelp output.
In order to distinguish it from other HTML output,
the webhelp output shows up under `/webhelp` after generation.
For now the webhelp is not copied during `mvn release`.
The files to start with are `webhelp/doc-name/index.html`.

**DOCS-81: Evaluate impact of a move to docbkx-tools 2.0.15**

This has not simply been evaluated, but also implemented.
The plugin now _requires_ 2.0.15.

**DOCS-117: Advertise latest stable doc release in in-progress documentation**

At present this change adds a small ribbon banner
with a link to the latest released doc in the upper right corner of HTML output.
No banner appears if the current HTML is in fact the latest stable release.

Because the script to generate a banner advertising the latest released doc
compares the current version with the latest version
to determine whether to show the banner,
the fix requires a new configuration parameter specifying the project version,
as in the following example:

    <projectVersion>3.0.0-SNAPSHOT</projectVersion>

The script relies on the latest versions being published as a JSON resource
that is safely accessible wherever the in-progress documentation is published.

TODO: Fix the default value for latestJson in AbstractBuildMojo.java,
and mention this at <https://github.com/markcraig/docs.forgerock.org>.

**DOCS-122: Center narrow images by default in PDF**

Moved to version 7993 of PlantUML by default,
which includes support for bidirectional arrows.

Use `<plantUmlVersion>` if necessary to set the artifact version.

**DOCS-127: Center narrow images by default in PDF**

**DOCS-139: Use &lt;htmlScript&gt; to add JavaScript to pages**

**DOCS-143: Left-Justify PDF Text**


## Bugs Fixed

**DOCS-76: Cannot copy/paste examples from PDF**

With previous versions of the doc build plugin, continuation characters clashed
with the JavaScript line flattening mechanism using in HTML output.

Authors can now use backslashes and carets as continuation characters
in `<screen>` content, as in the following example:

    <screen>
    $ mvn clean install && mvn jetty:run & \
     sleep 20 ; mvn exec:java \
     -Dexec.mainClass=org.forgerock.commons.doc.Main \
     -Dexec.args="./Users.json ./Groups.json" ; \
     fg
    </screen>

Continuation characters must be added to the document sources
in order for them to appear in output formats such as HTML and PDF.
Do leave a space at the outset of continued lines to allow folding.

**DOCS-124: Very tall images are not resized appropriately in the pdf output**

The resolution is a bit violent. Images taller than 5" are scaled to 5" high.

**DOCS-129: Doc build plugin should not overwrite site/doc/index.html**

If you want to keep a custom `index.html` file for your documentation set,
then set `-DkeepCustomIndexHtml=true` for the `release` goal configuration.

**DOCS-131: max-height CSS setting squashes tall images in HTML**

**DOCS-140: Improve readability of long commands**

You can now use `<userinput>` in `<screen>`, rendered in **bold** in HTML,
and `<computeroutput>` in `<screen>`, rendered in _italic_ in HTML.

As of this fix, SyntaxHighlighter is no longer applied for `<screen>`.

**DOCS-141: In Chrome, copy pasting code extracts from the html results in spaces being interpreted as special characters**

**DOCS-145: Nested ordered lists (within itemized lists) do not render correctly in the html**


## Known Issues

**DOCS-71: Soft hyphens displayed in mid line in PDF**

See <https://issues.apache.org/jira/browse/FOP-2239>.

Workaround: The problem might arise when you are documenting a synopsis
manually, as the markup is not available in the context where you want
to add a synopsis.

First, you can use `&#8230;` for horizontal ellipsis rather than `...`.

Second, if you have a construction like `.]` where brackets mean
optional, then add an extra space. It's technically wrong, but readers
will have to interpret the optional characters anyway.

**DOCS-132: Soft hyphens used to break lines are rendered in PDF as hyphen + space**

See <https://issues.apache.org/jira/browse/FOP-2358>.

Workaround: Fix the content after copy/paste.


* * *
This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2014 ForgeRock AS
