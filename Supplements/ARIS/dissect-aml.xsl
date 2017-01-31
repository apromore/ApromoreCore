<!--
  ~ Copyright © 2009-2017 The Apromore Initiative.
  ~
  ~ This file is part of "Apromore".
  ~
  ~ "Apromore" is free software; you can redistribute it and/or modify
  ~ it under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ "Apromore" is distributed in the hope that it will be useful, but
  ~ WITHOUT ANY WARRANTY; without even the implied warranty
  ~ of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  ~ See the GNU Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this program.
  ~ If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
  -->

<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:variable name="command">><![CDATA[
xsltproc --stringparam src "/Volumes/UNTITLED/GI - Building Blocks - ClaimCenter.xml" eepc_models.xsl /Volumes/UNTITLED/GI\ -\ Building\ Blocks\ -\ ClaimCenter.xml > script.sh
]]></xsl:variable>

<xsl:output method="text"/>

<xsl:param name="src" select="'/tmp/dummy'"/>
<xsl:param name="dest" select="'/tmp/Suncorp'"/>
<xsl:param name="bin" select="'/tmp'"/>

<xsl:template match="/">#!/bin/sh

SRC="<xsl:value-of select="$src"/>"
BINDIR="<xsl:value-of select="$bin"/>"
<xsl:apply-templates select="@*|node()"/>
</xsl:template>

<xsl:variable name="quot">"</xsl:variable>

<xsl:template match="Group">
  <xsl:variable name="dir" select="translate(AttrDef[@AttrDef.Type='AT_NAME']/AttrValue,'/','_')"/>
  <xsl:variable name="dir2">
    <xsl:choose>
    <xsl:when test="string-length($dir) = 0"><xsl:value-of select="$dest"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="$dir"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:text>mkdir "</xsl:text>
  <xsl:call-template name="escape">
    <xsl:with-param name="s" select="$dir2"/>
  </xsl:call-template>
  <xsl:text>"
</xsl:text>
  <xsl:text>cd "</xsl:text>
  <xsl:call-template name="escape">
    <xsl:with-param name="s" select="$dir2"/>
  </xsl:call-template>
  <xsl:text>"
</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>cd ..
</xsl:text>
</xsl:template>

<!-- Converts the string parameter "s" to escape all quotation marks with a leading backslash -->
<xsl:template name="escape">
  <xsl:param name="s"/>
  <xsl:choose>
  <xsl:when test="contains($s,$quot)">
    <xsl:value-of select="substring-before($s,$quot)"/>
    <xsl:text>\"</xsl:text>
    <xsl:call-template name="escape">
      <xsl:with-param name="s" select="substring-after($s,$quot)"/>
    </xsl:call-template>
  </xsl:when>
  <xsl:otherwise><xsl:value-of select="$s"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="Model[@Model.Type='MT_EEPC'][ObjOcc]">
  <xsl:text>echo Extracting "</xsl:text>
  <xsl:value-of select="AttrDef[@AttrDef.Type='AT_NAME']/AttrValue"/>
  <xsl:text>"
</xsl:text>
  <xsl:text>xsltproc --stringparam modelid </xsl:text>
  <xsl:value-of select="@Model.ID"/>
  <xsl:text> "$BINDIR/aml2model.xsl" "$SRC" > "</xsl:text>
  <xsl:call-template name="escape">
    <xsl:with-param name="s" select="translate(AttrDef[@AttrDef.Type='AT_NAME']/AttrValue,'/','_')"/>
  </xsl:call-template>
  <xsl:text>.aml"
</xsl:text>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:apply-templates select="@*|node()"/>
</xsl:template>

</xsl:transform>

