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
  !      Copyright 2012 ForgeRock AS
  !
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
 xmlns:d="http://docbook.org/ns/docbook" exclude-result-prefixes="d">
 <xsl:import href="urn:docbkx:stylesheet" />

 <xsl:preserve-space elements="d:programlisting"/>

 <xsl:template match="d:programlisting">
  <xsl:choose>
   <xsl:when test="@language='aci'">
    <pre class="brush: plain;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='csv'">
    <pre class="brush: plain;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='html'">
    <pre class="brush: html;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='http'">
    <pre class="brush: plain;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='ini'">
    <pre class="brush: plain;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='java'">
    <pre class="brush: java;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='javascript'">
    <pre class="brush: javascript;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='ldif'">
    <pre class="brush: plain;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='none'">
    <pre class="brush: plain;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='shell'">
    <pre class="brush: shell;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:when test="@language='xml'">
    <pre class="brush: xml;"><xsl:value-of select="." /></pre>
   </xsl:when>
   <xsl:otherwise>
    <pre class="brush: plain;"><xsl:value-of select="." /></pre>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:param name="make.clean.html" select="1" />
 <xsl:param name="docbook.css.link" select="0" />
 <xsl:param name="html.stylesheet">css/coredoc.css</xsl:param>
 <xsl:param name="admon.style">
  <xsl:value-of select="string('font-style: italic;')"></xsl:value-of>
 </xsl:param>
 <xsl:param name="default.table.frame">none</xsl:param>
 <xsl:param name="default.table.rules">none</xsl:param>
 <xsl:param name="table.cell.border.thickness">0pt</xsl:param>

 <xsl:param name="chunk.section.depth" select="0" />
 <xsl:param name="chunker.output.encoding">UTF-8</xsl:param>
 <xsl:param name="generate.legalnotice.link" select="1" />
 <xsl:param name="root.filename">index</xsl:param>
 <xsl:param name="use.id.as.filename" select="1" />

 <xsl:param name="generate.toc">
  appendix  nop
  article/appendix  nop
  article   nop
  book      toc,title
  chapter   toc,title
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
 <!-- <xsl:param name="toc.section.depth" select="1" /> -->
 <xsl:param name="toc.max.depth" select="1" />
 <xsl:param name="generate.meta.abstract" select="1" />

 <xsl:param name="use.extensions" select="1" />
</xsl:stylesheet>
