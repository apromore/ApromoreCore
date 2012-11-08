<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:bpmn   = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi = "http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:dc     = "http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di     = "http://www.omg.org/spec/DD/20100524/DI"
  xmlns:xsl    = "http://www.w3.org/1999/XSL/Transform">

<!-- Brutally force namespaces into having standard prefixes -->

<xsl:template match="bpmn:definitions">
    <bpmn:definitions xmlns="http://www.apromore.org/bpmn/3dc3e17f-5e19-41c8-b479-523e93844ff2#"
                      xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                      xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                      xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                      xmlns:di="http://www.omg.org/spec/DD/20100524/DI">
        <xsl:apply-templates select="@*|node()"/>
    </bpmn:definitions>
</xsl:template>

<xsl:template match="bpmn:*">
    <xsl:element name="bpmn:{local-name()}" namespace="http://www.omg.org/spec/BPMN/20100524/MODEL">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
</xsl:template>

<xsl:template match="bpmndi:*">
    <xsl:element name="bpmndi:{local-name()}" namespace="http://www.omg.org/spec/BPMN/20100524/DI">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
</xsl:template>

<xsl:template match="dc:*">
    <xsl:element name="dc:{local-name()}" namespace="http://www.omg.org/spec/DD/20100524/DC">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
</xsl:template>

<xsl:template match="di:*">
    <xsl:element name="di:{local-name()}" namespace="http://www.omg.org/spec/DD/20100524/DI">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
</xsl:template>

<!-- Each of BPMN's peculiar QName references needs to be individually corrected -->

<xsl:template match="@bpmnElement">
    <xsl:attribute name="bpmnElement">
        <xsl:choose>
        <xsl:when test="contains(current(),':')"><xsl:value-of select="substring-after(current(), ':')"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="current()"/></xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
</xsl:template>

<!-- Use an identity template so that everything that doesn't need workarounds gets passed through unchanged. -->
<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
