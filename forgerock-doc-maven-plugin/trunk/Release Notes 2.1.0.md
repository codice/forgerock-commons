# DRAFT - IN PROGRESS

# ForgeRock Documentation Tools 2.1.0 Release Notes

ForgeRock Documentation Tools is a catch all for the doc build plugin,
sites where we post documentation, and the documentation about
documentation. The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.

This release includes the following improvements, new features, and bug
fixes.

## Improvements & New Features

**DOCS-81: Evaluate impact of a move to docbkx-tools 2.0.15**

This has not simply been evaluated, but also implemented.
The plugin now _requires_ 2.0.15.

**DOCS-127: Center narrow images by default in PDF**


## Bugs Fixed

**DOCS-124: Very tall images are not resized appropriately in the pdf output**

The resolution is a bit violent. Images taller than 5" are scaled to 5" high.

**DOCS-129: Doc build plugin should not overwrite site/doc/index.html**

If you want to keep a custom `index.html` file for your documentation set,
then set `-DkeepCustomIndexHtml=true` for the `release` goal configuration.

**DOCS-131: max-height CSS setting squashes tall images in HTML**


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

**DOCS-76: Cannot copy/paste examples from PDF**

`<screen>` content is formatted for readability, but without backslashes
before newlines, so cannot be copy/pasted directly from the PDF.

Workaround: Access the HTML version, click on the [-] icon to flatten
the formatted example, and then copy the resulting content. (not
accessible)

* * *
This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2014 ForgeRock AS
