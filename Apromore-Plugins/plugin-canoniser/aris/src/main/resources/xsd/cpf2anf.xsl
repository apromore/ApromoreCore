<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0"
  xmlns:anf="http://www.apromore.org/ANF"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
  <anf:Annotations uri="dummy">
  <xsl:apply-templates/>
  </anf:Annotations>
</xsl:template>

<xsl:template match="attribute[@name='anf:Annotation']">
  <xsl:copy-of select="*"/>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:apply-templates select="@*|node()"/>
</xsl:template>

</xsl:transform>

