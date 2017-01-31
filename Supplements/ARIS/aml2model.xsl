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

