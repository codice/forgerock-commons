# DRAFT IN PROGRESS

Latest release is 2.1.3. These are draft release notes.


# ForgeRock Documentation Tools 3.0.0 Release Notes

ForgeRock Documentation Tools is a catch all for
the doc Maven plugin,
default branding and common content,
sites where we post documentation,
and the documentation about documentation.

The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.


## Compatibility

The fix DOCS-108 is a major refactoring of the doc Maven plugin.

As a result of the refactoring, the plugin now has only three goals:

*   A `pre-site` phase `build` goal to generate output
*   A `site` phase `site` goal to copy documents to a site layout
*   A `site` phase `release` goal to copy documents to a release layout

All configuration elements can be used in the top-level plugin configuration,
rather than the per-execution configurations.

These changes mean that you must update your POM in order to use this version.
See the README for this version for details.


## What's New

**DOCS-187: Documentation for legacy versions should not have the report bug footer**

The fix for this relies on a list of EOSL versions.
If the current version matches a version in the EOSL list,
then the footer is not shown in the HTML pages.

For the full list of EOSL versions, see the
[EOSL page at support.forgerock.com](https://support.forgerock.com/entries/24898647-Product-release-and-EOSL-dates).

**DOCS-178: Apply Maven resource filtering to .txt files as well**

The fix for this issue addresses both .txt and also .json files.


**DOCS-108: Reconsider forgerock-doc-maven-plugin architecture**

The fix for this issue is a major refactoring of the doc Maven plugin,
and a simplification of the plugin configuration model.

The new architecture is described in the Design document.


## Fixes

**DOCS-179: Issue with Maven HTML builds and images**

**DOCS-59: Only the draft documents are optimized to appear in Google searches; the final docs need to be SEO too.**

The fix adds a robots meta tag (noindex, nofollow) to site HTML,
that it then removes from release HTML.

## Known Issues

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
