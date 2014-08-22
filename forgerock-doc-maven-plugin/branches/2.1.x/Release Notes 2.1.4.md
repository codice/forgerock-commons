# DRAFT IN PROGRESS

# ForgeRock Documentation Tools 2.1.4 Release Notes

ForgeRock Documentation Tools is a catch all for the doc build artifacts,
sites where we post release documentation,
and the documentation about documentation.

The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.

This release brings the following changes,
and has the following known issues.

## Compatibility

TODO


## Improvements & New Features

**DOCS-194: Add option to allow merge of generated and docbkx source directories**

You can now use the maven configuration `<overwriteGeneratedSource>true</overwriteGeneratedSource>`
together the boilerplate goal to allow to copy and overwrites files from docbkxSourceDirectory to docbkxGeneratedSourceDirectory 

The default value is 'false' that does not change the default behaviour.


## Bugs Fixed

**DOCS-59: Only the draft documents are optimized to appear in Google searches; the final docs need to be SEO too.**

The fix adds a robots meta tag (noindex, nofollow) to site HTML,
that it then removes from release HTML.


## Known Issues

**DOCS-132: Soft hyphens used to break lines are rendered in PDF as hyphen + space**

Although soft hyphens are not used in this release,
the line break for hyphenation still remains.

See <https://issues.apache.org/jira/browse/FOP-2358>.

Workaround: Fix the content after copy/paste.

**DOCS-150: When a code sample is unwrapped, it it not limited to the width of the page**

**DOCS-163: The performance="optional" attr in a step has no effect**


* * *

This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2014 ForgeRock AS
