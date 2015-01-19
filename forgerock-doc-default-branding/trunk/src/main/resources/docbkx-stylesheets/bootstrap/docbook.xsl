<?xml version='1.0'?>
 <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 xmlns:ng="http://docbook.org/docbook-ng"
                 xmlns:db="http://docbook.org/ns/docbook"
                 xmlns:exsl="http://exslt.org/common"
                 xmlns:exslt="http://exslt.org/common"
                 exclude-result-prefixes="db ng exsl exslt"
                 version='1.0'>


<!-- ********************************************************************
     $Id: docbook.xsl 9605 2012-09-18 10:48:54Z tom_schr $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<xsl:template match="*" mode="process.root">
  <xsl:variable name="doc" select="self::*"/>

  <xsl:call-template name="user.preroot"/>
  <xsl:call-template name="root.messages"/>

  <html>
    <xsl:call-template name="root.attributes"/>
    <head>
      <xsl:call-template name="system.head.content">
        <xsl:with-param name="node" select="$doc"/>
      </xsl:call-template>
      <xsl:call-template name="head.content">
        <xsl:with-param name="node" select="$doc"/>
      </xsl:call-template>
      <xsl:call-template name="user.head.content">
        <xsl:with-param name="node" select="$doc"/>
      </xsl:call-template>
    </head>
    <body>
     <xsl:call-template name="body.attributes"/>
     <!-- Add bootstrap page elements //-->
     <!-- Add bootstrap header nav bar //-->
      <div class="navbar navbar-inverse navbar-fixed-top">
       <nav class="container-fluid">
        <div class="navbar-header">
         <a class="navbar-brand" href="#">ForgeRock</a>
        </div>
       </nav>
      </div>
     <!-- Add bootstrap full width banner, for doc title //-->
     <div class="jumbotron">
      <div class="container-fluid">
       <h1>
        <xsl:value-of select="ancestor-or-self::db:book/db:info/db:title"/>
        <span> </span>
        <small><xsl:value-of select="ancestor-or-self::db:book/db:info/db:subtitle"/></small></h1>

       <p><xsl:value-of select="ancestor-or-self::db:book/db:info/db:abstract/db:para"/></p>
      </div>
     </div>
     <!-- Add container for background image //-->
      <div class="left-shape-content"></div>
     <!-- Add fluid container for full width main content section //-->
      <div class="container-fluid">
       <div class="row">
        <xsl:call-template name="user.header.content">
         <xsl:with-param name="node" select="$doc"/>
       </xsl:call-template>
       <xsl:apply-templates select="."/>
       <xsl:call-template name="user.footer.content">
         <xsl:with-param name="node" select="$doc"/>
       </xsl:call-template>
        <!-- Add container for back-to-top button //-->
        <a href="#" class="back-to-top hidden-xs">
         <button type="button" class="btn btn-primary btn-default">
          <span class="glyphicon glyphicon-chevron-up"></span></button>
        </a>
       </div>
      </div>
     <!-- Add bootstrap footer bar //-->
        <div class="footer">
         <div class="container-fluid">
          <div class="footer-left"><span class="footer-item">Copyright ©
           2011-2015 ForgeRock AS</span></div>
          <div class="footer-right"><a target="_blank"
                                       class="footer-item snap-left"
                                       href="legalnotice.html"><i
           class="glyphicon glyphicon-briefcase"></i> Legal Notice</a> <a
           target="_blank" class="footer-item snap-left"
           href="https://bugster.forgerock.org/jira/secure/CreateIssueDetails!init.jspa?pid=10000&amp;components=10007&amp;issuetype=1"><i class="glyphicon glyphicon-ok-sign"></i> Corrections</a><a target="_blank" class="footer-item snap-left" href="#" data-toggle="modal" data-target="#myModal"><i class="glyphicon glyphicon-info-sign"></i> About</a> </div>
         </div>
        </div>
    </body>
  </html>
  
  <!-- Generate any css files only once, not once per chunk -->
  <xsl:call-template name="generate.css.files"/>
</xsl:template>

</xsl:stylesheet>
