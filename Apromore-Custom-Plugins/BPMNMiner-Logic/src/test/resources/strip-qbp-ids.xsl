<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns      = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmn = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:qbp  = "http://www.qbp-simulator.com/Schema201212"
  xmlns:xsl  = "http://www.w3.org/1999/XSL/Transform">

<!-- Erase UUIDs introduced by the QBP elements, so that we can test by straightforward file comparison -->
<xsl:template match="qbp:element/@id"/>
<xsl:template match="qbp:processSimulationInfo/@id"/>
<xsl:template match="qbp:resource/@id"/>
<xsl:template match="qbp:resourceId/text()"/>

<!-- Use an identity template so that everything that doesn't need workarounds gets passed through unchanged. -->
<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
