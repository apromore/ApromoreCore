<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:epml = "http://www.epml.de"
  xmlns:xsl  = "http://www.w3.org/1999/XSL/Transform">

<xsl:output encoding="UTF-8"/>

<xsl:variable name="ids" select="//@id[not(parent::epc)]"/>

<xsl:variable name="epcIds" select="//@epcId | //epc[not(@epcId)]/@id"/>

<!-- Calculate the maximum @id number -->
<xsl:variable name="max-id">
    <xsl:for-each select="$ids">
        <xsl:sort data-type="number"/>
        <xsl:if test="position() = last()">
            <xsl:value-of select="current()"/>
        </xsl:if>
    </xsl:for-each>
</xsl:variable>

<!-- Calculate the maximum @epcId number -->
<xsl:variable name="max-epcId">
    <xsl:for-each select="$epcIds">
        <xsl:sort data-type="number"/>
        <xsl:if test="position() = last()">
            <xsl:value-of select="current()"/>
        </xsl:if>
    </xsl:for-each>
</xsl:variable>

<!-- If the top level <epml> element isn't namespaced, add the right namespace -->
<!-- If there's no top level <coordinates>, insert one -->
<!-- If there's no top level <directory>, insert one -->
<xsl:template match="/epml:epml | epml">
    <xsl:element name="epml:epml">
        <xsl:apply-templates select="@*"/>
        <xsl:choose>
        <xsl:when test="not(coordinates)">
            <xsl:apply-templates select="graphicsDefault"/>
            <coordinates xOrigin="leftToRight" yOrigin="topToBottom"/>
            <xsl:apply-templates select="definitions | attributeTypes | directory"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="node()[name() != 'epc']"/>
        </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="not(directory)">
            <directory>
                <xsl:apply-templates select="epc"/>
            </directory>
        </xsl:if>
    </xsl:element>
</xsl:template>

<!-- If an <epc> element has no @epcID but does have an @id, use that instead -->
<xsl:template match="epc[not(@epcId)]">
    <xsl:copy>
        <xsl:call-template name="epcId-template">
            <xsl:with-param name="epcId-param" select="@id"/>
        </xsl:call-template>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

<!-- Drop the epc/@id attribute if we're replacing it with epc/@epcId -->
<xsl:template match="epc[not(@epcId)]/@id"/>

<!-- If epc/@epcId is zero, renumber it -->
<xsl:template match="epc/@epcId" name="epcId-template">
    <xsl:param name="epcId-param" select="current()"/>
    <xsl:attribute name="epcId">
        <xsl:choose>
        <xsl:when test="$epcId-param = 0">
            <xsl:value-of select="$max-epcId + 1"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$epcId-param"/>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
</xsl:template>

<!-- If epc component id is zero, renumber it -->
<xsl:template match="*[name() != 'epc']/@id | flow/@source | flow/@target" name="rule1">
    <xsl:attribute name="{name()}">
        <xsl:choose>
            <xsl:when test="current() = 0">
                <xsl:value-of select="$max-id + 1"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="current()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
</xsl:template>

<!-- Renumber arcs if their id is the same as any event OR if they happen to be zero -->
<xsl:template match="arc[(@id = //epc/*[name() != 'arc']/@id) or (@id = 0)]/@id">
    <xsl:attribute name="id">
        <xsl:value-of select="current() + $max-id + count(//*[name() != 'epc'][@id = 0])"/>
        <!-- the count term above corrects for the case in which #rule1 uses
             $max-id + 1 to label a non-arc component which had @id of 0 -->
    </xsl:attribute>
</xsl:template>

<!-- Use an identity template so that everything that doesn't need workarounds gets passed through unchanged. -->
<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
