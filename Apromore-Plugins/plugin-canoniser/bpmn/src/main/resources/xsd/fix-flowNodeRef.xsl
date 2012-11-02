<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns      = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmn = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:xsl  = "http://www.w3.org/1999/XSL/Transform">

<!-- The XJC compiler that generates JAXB for the BPMN schema doesn't handle collections of IDREFs well.
     This transformation fixes or works around each occurence of such elements in the schema. -->

<!-- Work around XJC's trouble with bpmn:dataSource/sourceRef.
     The Java classes BpmnDataInputAssociation and BpmnDataOutputAssociation insert the @workaround attributes -->
<xsl:template match="bpmn:dataInputAssociation/*|bpmn:dataOutputAssociation/*">
    <sourceRef><xsl:value-of select="../@workaround"/></sourceRef>
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>
<xsl:template match="@workaround"/>

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
