<?xml version="1.0" encoding="utf8"?>

<!--
  ~ Copyright © 2009-2016 The Apromore Initiative.
  ~
  ~ This file is part of "Apromore".
  ~
  ~ "Apromore" is free software; you can redistribute it and/or modify
  ~ it under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ "Apromore" is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this program.
  ~ If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
  -->

<!DOCTYPE transform [
  <!ENTITY STnodes         "ST_EV ST_FUNC ST_OPR_AND_1 ST_OPR_OR_1 ST_OPR_XOR_1 ST_OPR_XOR_1 ST_PRCS_IF ST_SYS_FUNC_ACT">
  <!ENTITY STobjects       "ST_DOC ST_DOC_KNWLDG_1 ST_INFO_CARR ST_INFO_CARR_NOTE ST_LIST">
  <!ENTITY STresourceTypes "ST_APPL_SYS ST_EMPL_TYPE ST_ORG_UNIT_1 ST_PERS_EXT ST_POS">
]>

<!--
ST_AUTH_COND
ST_CLST
ST_OBJCTV
ST_RISK_1
ST_SCRN
ST_TECH_TERM
-->

<!-- Translate ARIS Export XML into Apromore CPF -->

<xsl:transform version="1.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:anf="http://www.apromore.org/ANF"
  xmlns:cpf="http://www.apromore.org/CPF">

<xsl:output method="xml" indent="yes"/>

<!-- Scale is applied to dimensions of generated ANF Graphics elements -->
<xsl:variable name="scale" select="0.5"/>

<!-- Entry point if the input is a preprocessed individual EEPC file -->
<xsl:template match="fragment">
  <xsl:apply-templates select="Model"/>
</xsl:template>

<!-- Entry point if the input is a complete AML repository -->
<xsl:template match="AML">
  <xsl:apply-templates select="Group"/>
</xsl:template>

<xsl:template match="Group">
  <xsl:apply-templates select="Group|Model[@Model.ID='Model.gp2---10----u--']"/>
</xsl:template>

<!-- CPF Net -->
<xsl:template match="Model[@Model.Type='MT_EEPC']">
<cpf:CanonicalProcess uri="dummy" name="Dummy" version="1.0">
  <Net id="{@Model.ID}">
    <xsl:variable name="name" select="AttrDef[@AttrDef.Type='AT_NAME']/AttrValue"/>
    <xsl:if test="$name">
      <name><xsl:value-of select="$name"/></name>
    </xsl:if>

    <!-- Nodes -->
    <xsl:apply-templates select="ObjOcc[contains('&STnodes;',@SymbolNum)]"/>

    <!-- Edges -->
    <xsl:apply-templates select="ObjOcc[contains('&STnodes;',@SymbolNum)]/CxnOcc[@ToObjOcc.IdRef=../../ObjOcc[contains('&STnodes;',@SymbolNum)]/@ObjOcc.ID]"/>

    <!-- Objects -->
    <xsl:apply-templates select="ObjOcc[contains('&STobjects;',@SymbolNum)]"/>

  </Net>

  <!-- Resources -->
  <xsl:apply-templates select="ObjOcc[contains('&STresourceTypes;',@SymbolNum)]"/>

</cpf:CanonicalProcess>
</xsl:template>

<!-- CPF Node -->
<xsl:template match="ObjOcc[contains('&STnodes;',@SymbolNum)]">
  <xsl:variable name="ObjDef.IdRef" select="@ObjDef.IdRef"/>
  <xsl:variable name="ObjDef" select="//ObjDef[@ObjDef.ID=$ObjDef.IdRef]"/>
  <xsl:variable name="xsiType">
    <xsl:choose>
    <xsl:when test="$ObjDef/@TypeNum='OT_EVT'">cpf:EventType</xsl:when>
    <xsl:when test="$ObjDef/@TypeNum='OT_FUNC'">
      <xsl:choose>
      <xsl:when test="@SymbolNum='ST_PRCS_IF'">cpf:EventType</xsl:when>
      <xsl:otherwise>cpf:TaskType</xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="$ObjDef/@TypeNum='OT_RULE'">
      <xsl:variable name="direction">
        <xsl:choose>
        <xsl:when test="count(CxnOcc) &gt; 1">Split</xsl:when>
        <xsl:otherwise>Join</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:choose>
      <xsl:when test="@SymbolNum='ST_OPR_AND_1'">cpf:AND<xsl:value-of select="$direction"/>Type</xsl:when>
      <xsl:when test="@SymbolNum='ST_OPR_OR_1'">cpf:OR<xsl:value-of select="$direction"/>Type</xsl:when>
      <xsl:when test="@SymbolNum='ST_OPR_XOR_1'">cpf:XOR<xsl:value-of select="$direction"/>Type</xsl:when>
      <xsl:otherwise><xsl:message>Could not determine CPF Node xsi:type for AML eEPC Rule <xsl:value-of select="@ObjOcc.ID"/>.</xsl:message></xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise><xsl:message>Could not determine CPF Node xsi:type for AML eEPC ObjOcc <xsl:value-of select="@ObjOcc.ID"/>, symbol <xsl:value-of select="@SymbolNum"/>.</xsl:message></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <Node id="{@ObjOcc.ID}" xsi:type="{$xsiType}">
    <xsl:variable name="name" select="$ObjDef/AttrDef[@AttrDef.Type='AT_NAME']/AttrValue"/>
    <xsl:if test="$name">
      <name><xsl:value-of select="$name"/></name>
    </xsl:if>
    <xsl:if test="@SymbolNum='ST_PRCS_IF'">
      <attribute name="processInterface"/>
    </xsl:if>
    <xsl:if test="@SymbolNum='ST_SYS_FUNC_ACT'">
      <attribute name="systemAction"/>
    </xsl:if>

    <!-- resourceTypeRef (incoming, i.e ResourceType to Node) -->
    <xsl:apply-templates select="../ObjOcc[contains('&STresourceTypes;',@SymbolNum)]/CxnOcc[@ToObjOcc.IdRef=current()/@ObjOcc.ID]"/>

    <!-- resourceTypeRef (outgoing, i.e. Node to ResourceType -->
    <xsl:apply-templates select="CxnOcc[@ToObjOcc.IdRef=../../ObjOcc[contains('&STresourceTypes;',@SymbolNum)]/@ObjOcc.ID]"/>

    <!-- objectRef (incoming, i.e Object to Node) -->
    <xsl:apply-templates select="../ObjOcc[contains('&STobjects;',@SymbolNum)]/CxnOcc[@ToObjOcc.IdRef=current()/@ObjOcc.ID]"/>

    <!-- objectRef (outgoing, i.e. Node to Object) -->
    <xsl:apply-templates select="CxnOcc[@ToObjOcc.IdRef=../../ObjOcc[contains('&STobjects;',@SymbolNum)]/@ObjOcc.ID]"/>

    <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@ObjOcc.ID"/>
    </xsl:call-template>
  </Node>
</xsl:template>

<!-- CPF Edge (Node to Node) -->
<xsl:template match="ObjOcc/CxnOcc[@ToObjOcc.IdRef=../../ObjOcc[contains('&STnodes;',@SymbolNum)]/@ObjOcc.ID]">
  <xsl:variable name="CxnDef.IdRef" select="@CxnDef.IdRef"/>
  <xsl:variable name="CxnDef" select="//CxnDef[@CxnDef.ID=$CxnDef.IdRef]"/>
  <Edge id="{@CxnOcc.ID}" sourceId="{../@ObjOcc.ID}" targetId="{@ToObjOcc.IdRef}">
    <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@CxnOcc.ID"/>
    </xsl:call-template>
  </Edge>
</xsl:template>

<!-- CPF objectRef (Object to Node) -->
<xsl:template match="ObjOcc[contains('&STobjects;',@SymbolNum)]/CxnOcc[@ToObjOcc.IdRef=../../ObjOcc[contains('&STnodes;',@SymbolNum)]/@ObjOcc.ID]">
   <objectRef id="{@CxnOcc.ID}" objectId="{../@ObjOcc.ID}" type="input">
     <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@CxnOcc.ID"/>
    </xsl:call-template>
   </objectRef>
</xsl:template>

<!-- CPF objectRef (Node to Object) -->
<xsl:template match="ObjOcc[contains('&STnodes;',@SymbolNum)]/CxnOcc[@ToObjOcc.IdRef=../../ObjOcc[contains('&STobjects;',@SymbolNum)]/@ObjOcc.ID]">
   <objectRef id="{@CxnOcc.ID}" objectId="{@ToObjOcc.IdRef}" type="output">
     <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@CxnOcc.ID"/>
    </xsl:call-template>
   </objectRef>
</xsl:template>

<!-- CPF resourceTypeRef (ResourceType to Node) -->
<xsl:template match="ObjOcc[contains('&STresourceTypes;',@SymbolNum)]/CxnOcc[@ToObjOcc.IdRef=../../ObjOcc[contains('&STnodes;',@SymbolNum)]/@ObjOcc.ID]">
   <resourceTypeRef id="{@CxnOcc.ID}" resourceTypeId="{../@ObjOcc.ID}">
     <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@CxnOcc.ID"/>
    </xsl:call-template>
   </resourceTypeRef>
</xsl:template>

<!-- CPF resourceTypeRef (Node to ResourceType) -->
<xsl:template match="ObjOcc[contains('&STnodes;',@SymbolNum)]/CxnOff[@ToObjOcc.IdRef=../../ObjOcc[contains('&STresourceTypes;',@SymbolNum)]/@ObjOcc.ID]">
   <resourceTypeRef id="{@CxnOcc.ID}" resourceTypeId="{@ToObjOcc.IdRef}">
     <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@CxnOcc.ID"/>
    </xsl:call-template>
   </resourceTypeRef>
</xsl:template>

<!-- skip connections that don't satisfy the preceding templates -->
<xsl:template match="CxnOcc"/>

<!-- CPF Object -->
<xsl:template match="ObjOcc[@SymbolNum='ST_DOC']          |
                     ObjOcc[@SymbolNum='ST_DOC_KNWLDG_1'] |
                     ObjOcc[@SymbolNum='ST_INFO_CARR']    |
                     ObjOcc[@SymbolNum='ST_INFO_CARR_NOTE']">
  <xsl:variable name="ObjDef.IdRef" select="@ObjDef.IdRef"/>
  <xsl:variable name="ObjDef" select="//ObjDef[@ObjDef.ID=$ObjDef.IdRef]"/>
  <Object id="{@ObjOcc.ID}">
    <xsl:variable name="name" select="$ObjDef/AttrDef[@AttrDef.Type='AT_NAME']/AttrValue"/>
    <xsl:if test="$name">
      <name><xsl:value-of select="$name"/></name>
    </xsl:if>
    <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@ObjOcc.ID"/>
    </xsl:call-template>
  </Object>
</xsl:template>

<!-- CPF ResourceType -->
<xsl:template match="ObjOcc[@SymbolNum='ST_APPL_SYS']  |
                     ObjOcc[@SymbolNum='ST_EMPL_TYPE'] |
                     ObjOcc[@SymbolNum='ST_ORG_UNIT_1']  |
                     ObjOcc[@SymbolNum='ST_PERS_EXT']  |
                     ObjOcc[@SymbolNum='ST_POS']">
  <xsl:variable name="ObjDef.IdRef" select="@ObjDef.IdRef"/>
  <xsl:variable name="ObjDef" select="//ObjDef[@ObjDef.ID=$ObjDef.IdRef]"/>
  <ResourceType id="{@ObjOcc.ID}">
    <xsl:variable name="name" select="$ObjDef/AttrDef[@AttrDef.Type='AT_NAME']/AttrValue"/>
    <xsl:choose>
    <xsl:when test="@SymbolNum='ST_APPL_SYS'">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">cpf:NonhumanType</xsl:attribute>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_EMPL_TYPE'">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">cpf:HumanType</xsl:attribute>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_ORG_UNIT_1'">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">cpf:HumanType</xsl:attribute>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_PERS_EXT'">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">cpf:HumanType</xsl:attribute>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_POS'">
      <xsl:attribute name="type" namespace="http://www.w3.org/2001/XMLSchema-instance">cpf:HumanType</xsl:attribute>
    </xsl:when>
    </xsl:choose>
    <xsl:if test="$name">
      <name><xsl:value-of select="$name"/></name>
    </xsl:if>
    <xsl:choose>
    <xsl:when test="@SymbolNum='ST_APPL_SYS'">
      <type>SoftwareSystem</type>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_EMPL_TYPE'">
      <type>Role</type>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_ORG_UNIT_1'">
      <type>Unit</type>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_PERS_EXT'">
      <type>Participant</type>
    </xsl:when>
    <xsl:when test="@SymbolNum='ST_POS'">
      <type>Role</type>
    </xsl:when>
    </xsl:choose>
    <xsl:call-template name="GraphicsType">
      <xsl:with-param name="id" select="@ObjOcc.ID"/>
    </xsl:call-template>
  </ResourceType>
</xsl:template>

<!-- ANF Graphics -->
<xsl:template name="GraphicsType">
    <xsl:param name="id"/>
    <attribute name="anf:Annotation">
      <Annotation xsi:type="anf:GraphicsType" id="{$id}" cpfId="{$id}">
        <xsl:for-each select="Position">
          <position x="{@Pos.X * $scale}" y="{@Pos.Y * 0.5}"/>
        </xsl:for-each>
        <xsl:for-each select="Size">
          <size width="{@Size.dX * 0.5}" height="{@Size.dY * 0.5}"/>
        </xsl:for-each>
      </Annotation>
    </attribute>
</xsl:template>

<!-- De-canonization exception messages -->

<xsl:template match="Model[@Model.Type='MT_EEPC'][not(ObjOcc)]">
  <xsl:message>Model with ID <xsl:value-of select="@Model.ID"/> has no ObjOcc children.</xsl:message>
</xsl:template>

<xsl:template match="Model">
  <xsl:message>Model with ID <xsl:value-of select="@Model.ID"/> is of unsupported type <xsl:value-of select="@Model.Type"/>.</xsl:message>
</xsl:template>

<xsl:template match="ObjOcc">
  <xsl:message>ObjOcc with ID <xsl:value-of select="@ObjOcc.ID"/> is of unsupported symbol <xsl:value-of select="@SymbolNum"/>.</xsl:message>
</xsl:template>

<!-- Finally, anything that's been entirely missed is passed through to the output so we'll notice it -->
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:transform>

