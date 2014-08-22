# ForgeRock Doc Build Maven Plugin Design

The ForgeRock Doc Build Maven Plugin implementation grew organically
with little thought to any overall design.

Initially the plugin merely replaced POM-based configuration
with a uniform mechanism to apply across ForgeRock projects.
The organization followed the structure of the POM-based configuration.

Later additional features were added to the existing structure.

The haphazard arrangement of code made the implementation
difficult to learn and to maintain.

A more tractable implementation follows an easy-to-understand design,
one that resembles a pipeline having the following stages.

## Pre-Processing Source Files

The first stage pre-processes DocBook XML source files.

A variety of pre-processing tasks are performed in the following order:

*  Unpack the branding elements. (`Branding`)
*  Make a modifiable copy of the original sources. (`ModifiableCopy`)
*  Augment the copy with common content. (`CommonContent`)
*  Include Java code by applying the JCite plugin. (`JCite`)
*  Perform Maven resource filtering on the copy to replace variables. (`Filter`)
*  Edit `<imagedata>` elements in the resource filtered files. (`ImageData`)
*  Perform image generation on the resource filtered files. (`PlantUml`)
*  Set DPI on .png images in the resource filtered files. (`Dpi`)
*  Perform additional pre-processing on the resource filtered files. (`CurrentDocId`)
*  When generating FO output, prepare fonts for use with Apache FOP. (`Fop`)
*  When generating HTML output, add custom CSS to the sources. (`CustomCss`)

This stage is performed during the `pre-site` phase, `build` goal. (`PreSiteMojo`)

## Processing Sources to Generate Output

The next stage generates output formats.

A build class named by output format encapsulates generation of each format,
including olink generation:

*  Chunked HTML (`ChunkedHtml`)
*  EPUB (`Epub`)
*  Man pages (`Manpage`)
*  PDF (`Pdf`, which is a wrapper for `Fo`)
*  RTF (`Rtf`, which is a wrapper for `Fo`)
*  Single-page HTML (`SingleHtml`)
*  Webhelp (`Webhelp`)

This stage is performed during the `pre-site` phase, `build` goal. (`PreSiteMojo`)

## Post-Processing Generated Output

The next stage post-processes generated output,
clearly separating post-processing from output generation.

Some formats such as HTML require fairly extensive post-processing
to include JavaScript, change CSS, and so forth.
Most formats currently require no post-processing.

*  HTML post-processing (`Html`)
*  No post-processing (`NoOp`)

This stage is performed during the `pre-site` phase, `build` goal. (`PreSiteMojo`)

## Copying Output to a Site Layout

The next stage copies output to the site directory,
following the standard layout for core documentation.

Documents show up under `${project.build.directory}/site/doc`.

This stage also includes link testing,
which is to avoid running potentially lengthy link tests in the `pre-site` phase.

*  Lay out built docs (`Layout`)
*  Add `.htaccess` for Apache HTTP Server (`Htaccess`)
*  Redirect `/doc/index.html` to `docs.html` (`Redirect`)
*  Link test (`LinkTest`)

This stage is performed during the `site` phase, `site` goal. (`SiteMojo`)

## Copying Output to a Release Layout

The final stage copies output to a release directory.

*  Lay out release docs (`Layout`)
*  Add an index.html to release docs (`IndexHtml`)
*  Rename PDFs in release docs to include the version number (`PdfNames`)
*  Fix favicon links in release HTML (`Favicon`)
*  Replace CSS in release HTML (`Css`)
*  Replace robots meta tag in release HTML (`Robots`)
*  Zip release docs (`Zip`)

This stage is performed during the `site` phase, `release` goal. (`ReleaseMojo`)


## Configuration

Configuration parameters and closely related methods
are handled through the `AbstractDocbkxMojo` class.

The other Mojo classes inherit from this abstract class.


## About the Java Packages

*  `org.forgerock.doc.maven`: top-level classes for configuration & overall operation
*  `org.forgerock.doc.maven.build`: classes for building output formats
*  `org.forgerock.doc.maven.post`: post-processing classes
*  `org.forgerock.doc.maven.pre`: pre-processing classes
*  `org.forgerock.doc.maven.release`: classes for preparing the release documents
*  `org.forgerock.doc.maven.site`: classes for preparing the project site
*  `org.forgerock.doc.maven.utils`: utility classes


* * *

This work is licensed under the Creative Commons
Attribution-NonCommercial-NoDerivs 3.0 Unported License.
To view a copy of this license, visit
<http://creativecommons.org/licenses/by-nc-nd/3.0/>
or send a letter to Creative Commons, 444 Castro Street,
Suite 900, Mountain View, California, 94041, USA.

Copyright 2014 ForgeRock AS
