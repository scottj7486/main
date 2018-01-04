<xsl:stylesheet version="1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" indent="yes" />
<xsl:template match="xsd:element[@ref='ansi-nist:FingerPositionCode' and ../../../../@name='FingerprintType']">
<xsd:element ref="ansi-nist:FingerPositionCode">
<xsl:attribute name="minOccurs">0</xsl:attribute>
<xsl:attribute name="maxOccurs">1</xsl:attribute>
</xsd:element>
</xsl:template>
<xsl:template match="xsd:enumeration[@value='0' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='1' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='10' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='11' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='12' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='13' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='14' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='15' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='19' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='2' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='255' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='3' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='4' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='5' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='6' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='7' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='8' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:enumeration[@value='9' and ../../@name='FPCCodeSimpleType']">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="/xsd:schema">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:complexType">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:simpleType">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:complexContent">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:extension">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:restriction">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:sequence">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:minInclusive">
<xsl:copy>
<xsl:for-each select="@*">
<xsl:copy />
</xsl:for-each>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:simpleContent">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="/xsd:schema/xsd:annotation">
<xsl:copy-of select="." />
</xsl:template>
<!-- <xsl:template match="/xsd:schema/xsd:complexType">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="/xsd:schema/xsd:simpleType">
<xsl:copy-of select="." />
</xsl:template> -->
<xsl:template match="/xsd:attribute/xsd:annotation">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="/xsd:attributeGroup/xsd:annotation">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:annotation" />
<xsl:template match="xsd:import">
<xsl:copy>
<xsl:attribute name="namespace">
<xsl:value-of select="@namespace" />
</xsl:attribute>
<xsl:attribute name="schemaLocation">
<xsl:value-of select="@schemaLocation" />
</xsl:attribute>
<xsl:apply-templates />
</xsl:copy>
</xsl:template>
<xsl:template match="xsd:attribute">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:attributeGroup">
<xsl:copy-of select="." />
</xsl:template>
<xsl:template match="xsd:element">
<xsl:copy-of select="." />
</xsl:template>
</xsl:stylesheet>
