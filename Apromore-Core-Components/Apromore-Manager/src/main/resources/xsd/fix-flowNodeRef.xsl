<?xml version="1.0" encoding="UTF-8"?>


<xsl:stylesheet version="1.0"
  xmlns      = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmn = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:xsl  = "http://www.w3.org/1999/XSL/Transform">

<!-- Work around XJC's trouble with bpmn:lane/flowNodeRef. -->
<xsl:template match="bpmn:lane/*">
    <xsl:element name="flowNodeRef">
        <xsl:value-of select="@id"/>
    </xsl:element>
</xsl:template>

<!-- Use an identity template so that everything that doesn't need workarounds gets passed through unchanged. -->
<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
