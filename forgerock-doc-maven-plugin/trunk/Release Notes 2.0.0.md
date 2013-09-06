#DRAFT 2.0.0 is currently in progress and not yet released

# ForgeRock Documentation Tools 2.0.0 Release Notes

ForgeRock Documentation Tools is a catch all for the doc build plugin,
sites where we post documentation, and the documentation about
documentation. The link to the online issue tracker is
<https://bugster.forgerock.org/jira/browse/DOCS>.

This release includes the following improvements, new features, and bug
fixes.

## Improvements & New Features

**DOCS-65: Move to mojo-executor 2.1.0 to take advantage of support for dependencies**

**DOCS-78: Include hyphen when splitting URL across lines**

**DOCS-85: Make it easy to get the link to other titled block elements**

Added ↪ on mouseover in HTML for all titles with anchors.

**DOCS-86: Leave more space between table cells in PDF**

**DOCS-87: Allow soft hyphens at commas in long literals**

**DOCS-88: Style more width in HTML table rendering of simplelist**

**DOCS-89: Simplify use of Maven properties in XML attribute values**

The fix requires that you insert a new execution as the first `pre-site` goal:

    <execution>
        <id>filter-sources</id>
        <phase>pre-site</phase>
        <goals>
            <goal>filter</goal>
        </goals>
    </execution>

## Bugs Fixed

**DOCS-75: Wide programlisting shading extends to the right edge of the page in PDF**

The fix helps, but for page-wide listings, use this suggestion from Bob Stayton:

    <informalexample>
    <?dbfo pgwide="1"?>
    <programlisting>Wide listing that needs full-page width ...</programlisting>
    </informalexample>

**DOCS-91: Doc build plugin fails when project names include numbers**

**DOCS-92: Doc build plugin NPE when source directory contains no directories**

**DOCS-93: Doc build plugin does not properly use configuration settings**

**DOCS-94: Version numbers on draft docs are confusing**

**DOCS-95: Do not set publication date on in-progress documentation**

This is fixed in the doc build plugin, but requires changes to the product POM:

      <!--
        Release date and publication date are set at release build time.
          -D"releaseDate=Software release date: January 1, 1970"
          -D"pubDate=Publication date: December 31, 1969"
        At all other times, the dates should be empty.
      -->
      <releaseDate />
      <softwareReleaseDate>${releaseDate}</softwareReleaseDate>
      <pubDate />
      <publicationDate>${pubDate}</publicationDate>

And to the top level document files, such as `index.xml`:

      <date><?eval ${publicationDate}?></date>
      <pubdate><?eval ${publicationDate}?></pubdate>
      <releaseinfo><?eval ${softwareReleaseDate}?></releaseinfo>

Once these are changed in the product docs and this version of the doc build
plugin is used, the publication date only appears in output when set.


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


* * *
This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2012-2013 ForgeRock AS
