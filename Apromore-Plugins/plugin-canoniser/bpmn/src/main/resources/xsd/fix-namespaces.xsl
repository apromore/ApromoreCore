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
  xmlns:bpmn   = "http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi = "http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:dc     = "http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di     = "http://www.omg.org/spec/DD/20100524/DI"
  xmlns:ixbpmn = "http://www.igrafx.com/2015/bpmn"
  xmlns:xalan  = "http://xml.apache.org/xslt"
  xmlns:xsl    = "http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes" xalan:indent-amount="2"/>

<!-- Brutally force namespaces into having standard prefixes -->

<xsl:template match="bpmn:definitions">
    <bpmn:definitions xmlns="genid:TARGET"
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

<!-- Strip iGrafx namespace -->

<xsl:template match="@ixbpmn:*"/>
<xsl:template match="ixbpmn:htmlText"/>

<!-- Each of BPMN's peculiar QName references needs to be individually corrected -->

<xsl:template match="@attachedToRef">
    <xsl:attribute name="attachedToRef">
        <xsl:choose>
        <xsl:when test="contains(current(),':')"><xsl:value-of select="substring-after(current(), ':')"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="current()"/></xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
</xsl:template>

<xsl:template match="@bpmnElement">
    <xsl:attribute name="bpmnElement">
        <xsl:choose>
        <xsl:when test="contains(current(),':')"><xsl:value-of select="substring-after(current(), ':')"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="current()"/></xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
</xsl:template>

<xsl:template match="bpmn:incoming/text()|bpmn:outgoing/text()">
    <xsl:choose>
    <xsl:when test="contains(current(),':')"><xsl:value-of select="substring-after(current(), ':')"/></xsl:when>
    <xsl:otherwise><xsl:value-of select="current()"/></xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- Use an identity template so that everything that doesn't need workarounds gets passed through unchanged. -->
<xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
