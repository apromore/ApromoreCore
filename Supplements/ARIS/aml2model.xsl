<!-- Translate ARIS Export XML into Apromore CPF -->


<xsl:transform version="1.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:anf="http://www.apromore.org/ANF"
  xmlns:cpf="http://www.apromore.org/CPF">

<xsl:output method="xml" indent="yes"/>

<xsl:param name="modelid" select="'Model.gp2---10----u--'"/>

<xsl:template match="AML">
<fragment>
  <xsl:variable name="ObjDef" select="//Model[@Model.ID=$modelid]//@ObjDef.IdRef"/>
  <xsl:apply-templates select="//ObjDef[@ObjDef.ID=$ObjDef]"/>
  <xsl:apply-templates select="//Model[@Model.ID=$modelid]"/>
</fragment>
</xsl:template>

  <!--
<xsl:template match="Group">
  <xsl:if test=".//Model[@Model.ID=$modelid]">
    <xsl:apply-templates select="ObjDef"/>
  </xsl:if>
  <xsl:apply-templates select="Group|Model[@Model.ID=$modelid]"/>
</xsl:template>
  -->

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:transform>

