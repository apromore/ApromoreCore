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

<xsl:transform version="1.0"
  xmlns:anf="http://www.apromore.org/ANF"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
  <anf:Annotations uri="dummy">
  <xsl:apply-templates/>
  </anf:Annotations>
</xsl:template>

<xsl:template match="attribute[@name='anf:Annotation']">
  <xsl:copy-of select="*"/>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:apply-templates select="@*|node()"/>
</xsl:template>

</xsl:transform>

