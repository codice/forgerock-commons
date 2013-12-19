<?xml version="1.0" encoding="UTF-8"?>
<!--
  ! This work is licensed under the Creative Commons
  ! Attribution-NonCommercial-NoDerivs 3.0 Unported License.
  ! To view a copy of this license, visit
  ! http://creativecommons.org/licenses/by-nc-nd/3.0/
  ! or send a letter to Creative Commons, 444 Castro Street,
  ! Suite 900, Mountain View, California, 94041, USA.
  !
  ! You can also obtain a copy of the license at
  ! legal/CC-BY-NC-ND.txt.
  ! See the License for the specific language governing permissions
  ! and limitations under the License.
  !
  ! If applicable, add the following below this CCPL HEADER, with the fields
  ! enclosed by brackets "[]" replaced with your own identifying information:
  !      Portions Copyright [yyyy] [name of copyright owner]
  !
  !      Copyright 2011-2013 ForgeRock AS
  !
-->
<xsl:stylesheet
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:d="http://docbook.org/ns/docbook"
xmlns:xlink="http://www.w3.org/1999/xlink"
exclude-result-prefixes="d"
version="1.0">

  <xsl:import href="urn:docbkx:stylesheet"/>
  <xsl:import href="titlepages.xsl"/>
  <xsl:import href="urn:docbkx:stylesheet/highlight.xsl"/>

  <xsl:param name="page.height.portrait">9in</xsl:param>
  <xsl:param name="page.width.portrait">7.5in</xsl:param>
  <xsl:param name="double.sided" select="1"></xsl:param>
  <xsl:param name="fop1.extensions" select="1"/>

  <xsl:attribute-set name="root.properties">
   <xsl:attribute name="orphans">5</xsl:attribute>
   <xsl:attribute name="widows">5</xsl:attribute>
  </xsl:attribute-set>

  <xsl:param name="body.font.master">9</xsl:param>
  <xsl:param name="body.font.family">DejaVuSerif</xsl:param>
  <xsl:param name="dingbat.font.family">DejaVuSerif</xsl:param>
  <xsl:param name="monospace.font.family">DejaVuSansMono</xsl:param>
  <xsl:param name="sans.font.family">DejaVuSans</xsl:param>
  <xsl:param name="title.font.family">DejaVuSans</xsl:param>

  <xsl:attribute-set name="section.title.level1.properties">
   <xsl:attribute name="font-size">14pt</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="section.title.level2.properties">
   <xsl:attribute name="font-size">12pt</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="section.title.level3.properties">
   <xsl:attribute name="font-size">11pt</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="section.title.level4.properties">
   <xsl:attribute name="font-size">10pt</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="section.title.level5.properties">
   <xsl:attribute name="font-size">10pt</xsl:attribute>
   <xsl:attribute name="font-weight">normal</xsl:attribute>
   <xsl:attribute name="font-style">italic</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:param name="generate.toc">
    appendix  nop
    article/appendix  nop
    article   nop
    book      toc,title
    chapter   nop
    part      toc,title
    preface   nop
    qandadiv  nop
    qandaset  nop
    reference toc,title
    sect1     nop
    sect2     nop
    sect3     nop
    sect4     nop
    sect5     nop
    section   nop
    set       toc,title
  </xsl:param>
  <xsl:param name="toc.max.depth">0</xsl:param>
  
  <xsl:param name="use.extensions" select="1"/>
  <xsl:param name="linenumbering.everyNth" select="1"/>
  <xsl:param name="orderedlist.label.width">1.8em</xsl:param>

  <xsl:param name="default.table.frame">topbot</xsl:param>
  <xsl:param name="default.table.rules">none</xsl:param>
  <xsl:param name="table.cell.border.thickness">0pt</xsl:param>
  
  <xsl:param name="variablelist.as.blocks" select="1"></xsl:param>
  <xsl:param name="variablelist.term.separator"></xsl:param>
  <xsl:param name="variablelist.term.break.after">1</xsl:param>
  
  <xsl:attribute-set name="monospace.properties">
   <xsl:attribute name="font-size">0.9em</xsl:attribute>
  </xsl:attribute-set>
  <xsl:param name="shade.verbatim" select="1"/>
  <xsl:attribute-set name="shade.verbatim.style">
   <xsl:attribute name="background-color">#d4d4d4</xsl:attribute>
   <xsl:attribute name="border">0.5pt dashed #626d75</xsl:attribute>
   <xsl:attribute name="padding">3pt</xsl:attribute>
   <xsl:attribute name="wrap-option">no-wrap</xsl:attribute>
   <xsl:attribute name="font-size">0.75em</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:param name="ulink.footnotes" select="0"/>
  <xsl:param name="ulink.show" select="0"/>
  <xsl:param name="ulink.hyphenate">&#xAD;</xsl:param>

  <!-- Hyphenate long literals at literal.hyphenate.chars
       Adapted from the hyphenate-url template. -->
  <xsl:param name="literal.hyphenate">&#xAD;</xsl:param>
  <xsl:param name="literal.hyphenate.chars">./,</xsl:param>
  <!-- soft hyphen: &#xAD; -->

  <xsl:template match="d:literal//text()">
    <xsl:call-template name="hyphenate-literal">
     <xsl:with-param name="literal" select="."/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="hyphenate-literal">
    <xsl:param name="literal" select="''"/>
    <xsl:choose>
      <xsl:when test="string-length($literal) &gt; 1">
        <xsl:variable name="char" select="substring($literal, 1, 1)"/>
        <xsl:value-of select="$char"/>
        <xsl:if test="contains($literal.hyphenate.chars, $char)">
          <!-- Do not hyphen in-between // -->
          <xsl:if test="not($char = '/' and substring($literal,2,1) = '/')">
            <xsl:copy-of select="$literal.hyphenate"/>
          </xsl:if>
        </xsl:if>
        <!-- recurse to the next character -->
        <xsl:call-template name="hyphenate-literal">
          <xsl:with-param name="literal" select="substring($literal, 2)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$literal"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Do not hyphenate in general. -->
  <xsl:param name="hyphenate">false</xsl:param>

  <!--
   When https://code.google.com/p/docbkx-tools/issues/detail?id=35
   is resolved, it might be nice to use admonition graphics
   rather than gray backgrounds.
  -->
  <xsl:attribute-set name="admonition.properties">
    <xsl:attribute name="background-color">#d4d4d4</xsl:attribute>
    <xsl:attribute name="border">0.5pt dashed #626d75</xsl:attribute>
    <xsl:attribute name="padding">3pt</xsl:attribute>
  </xsl:attribute-set>

  <!-- DOCS-75: Wide programlisting shading extends to the right edge of the page in PDF -->
  <xsl:param name="monospace.verbatim.font.width">0.445em</xsl:param>

  <!-- DOCS-86: Leave more space between table cells in PDF -->
  <xsl:attribute-set name="table.cell.padding">
    <xsl:attribute name="padding-left">8pt</xsl:attribute>
    <xsl:attribute name="padding-right">8pt</xsl:attribute>
    <xsl:attribute name="padding-top">2pt</xsl:attribute>
    <xsl:attribute name="padding-bottom">2pt</xsl:attribute>
  </xsl:attribute-set>
  <xsl:attribute-set name="xref.properties">
    <xsl:attribute name="color">#47a</xsl:attribute>
  </xsl:attribute-set>
</xsl:stylesheet>
