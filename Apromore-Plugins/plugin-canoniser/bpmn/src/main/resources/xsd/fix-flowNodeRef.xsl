<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ This file is part of "Apromore".
  ~
  ~ Copyright (C) 2019 - 2020 The University of Melbourne.
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

<xsl:stylesheet version="1.0"
  xmlns:bpmn = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:pc   = "http://www.processconfiguration.com"
  xmlns:xsl  = "http://www.w3.org/1999/XSL/Transform">

<!-- The XJC compiler that generates JAXB for the BPMN schema doesn't handle collections of IDREFs well.
     This transformation fixes or works around each occurence of such elements in the schema. -->

<!-- Work around XJC's trouble with bpmn:dataSource/sourceRef.
     The Java classes BpmnDataInputAssociation and BpmnDataOutputAssociation insert the @workaround attributes -->
<xsl:template match="bpmn:dataInputAssociation/*|bpmn:dataOutputAssociation/*">
    <bpmn:sourceRef><xsl:value-of select="../@workaround"/></bpmn:sourceRef>
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>
<xsl:template match="@workaround"/>

<!-- Attributes of type IDREFS aren't allowed to be zero length -->
<xsl:template match="pc:configuration/@sourceRefs[.='']"/>
<xsl:template match="pc:configuration/@targetRefs[.='']"/>

<!-- Work around XJC's trouble with bpmn:lane/flowNodeRef. -->
<xsl:template match="bpmn:lane/*">
    <xsl:element name="flowNodeRef" namespace="http://www.omg.org/spec/BPMN/20100524/MODEL">
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
