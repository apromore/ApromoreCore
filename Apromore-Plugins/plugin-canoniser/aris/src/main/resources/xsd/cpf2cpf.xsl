<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0"
  xmlns:anf="http://www.apromore.org/ANF"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes"/>

<xsl:template match="attribute[@name='anf:Annotation']"/>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:transform>

