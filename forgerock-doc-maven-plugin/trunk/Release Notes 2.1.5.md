# ForgeRock Documentation Tools 2.1.5 Release Notes

ForgeRock Documentation Tools is a catch all for the doc build artifacts,
sites where we post release documentation,
and the documentation about documentation.

The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.

## Compatibility

This release introduces a new configuration setting with the fix for DOCS-215.

It does not introduce any incompatible changes.


## Improvements & New Features

**DOCS-215: Add configuration for stopping at pre-processed DocBook**

This improvement introduces a boolean configuration parameter,
`<stopAfterPreProcessing>` (default: `false`).

When `<stopAfterPreProcessing>` is set to `true`,
the build stops when DocBook XML requires no further pre-processing.
The plugin logs a message indicating where to find the pre-processed files:

    [INFO] Pre-processed sources are available under ...


## Bugs Fixed

**DOCS-213: Linktester phase in doc build plugin fails to detect broken links**

**DOCS-200: US phone number for ForgeRock has changed**

**DOCS-206: Webhelp in-progress docs are not clearly marked DRAFT**

**DOCS-171: Make Secondary Sub-Headings More Prominent**


## Known Issues

**DOCS-224: Webhelp format; sections from one document appear temporarily as subsections in a second document**

**DOCS-220: Chapter numbers should be included in WebHelp format**

**DOCS-190: PDF: release date and publication date are not shown**

**DOCS-163: The performance="optional" attr in a step has no effect**

**DOCS-150: When a code sample is unwrapped, it it not limited to the width of the page**

**DOCS-132: Soft hyphens used to break lines are rendered in PDF as hyphen + space**

Although soft hyphens are not used in this release,
the line break for hyphenation still remains.

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
