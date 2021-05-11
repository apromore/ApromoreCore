<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns      = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmn = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:xsl  = "http://www.w3.org/1999/XSL/Transform">

<!-- Disallow any complex content (e.g. <img>, <script>) in human-legible elements by entering textOnly mode. -->
<xsl:template match="bpmn:documentation|bpmn:text">
    <xsl:copy>
        <xsl:apply-templates select="@*"/>
        <xsl:apply-templates select="node()" mode="textOnly"/>
    </xsl:copy>
</xsl:template>

<!-- Any <script> tags outside of textOnly mode will still be executed by bpmn.io, so we comment them out. -->
<xsl:template match="*[local-name()='script']">
    <xsl:comment>
        <xsl:apply-templates select="." mode="textOnly"/>
    </xsl:comment>
</xsl:template>

<!-- Use an identity template so that everything that doesn't need sanitization gets passed through unchanged. -->
<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>


<!-- Templates supporting textOnly mode -->

<!-- Inside an element which is disallowed complex content, text gets passed through unchanged. -->
<xsl:template match="text()" mode="textOnly">
    <xsl:value-of select="."/>
</xsl:template>

<!-- Inside an element which is disallowed complex content, elements are converted into text. -->
<xsl:template match="*" mode="textOnly">
    <xsl:value-of select="concat('&lt;', name())"/>
    <xsl:for-each select="@*">
        <xsl:value-of select="concat(' ', name(), '=&quot;', current(), '&quot;')"/>
    </xsl:for-each>
    <xsl:text>&gt;</xsl:text>
    <xsl:apply-templates select="node()" mode="textOnly"/>
    <xsl:value-of select="concat('&lt;/', name(), '&gt;')"/>
</xsl:template>

</xsl:stylesheet>

