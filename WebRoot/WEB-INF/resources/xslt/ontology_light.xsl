<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="no" indent="no" />

	<xsl:template match="/">
	<data>
		<list>
		<xsl:apply-templates />
		</list>
	</data>
	</xsl:template>

	<xsl:template match="ontology">
		<ontology>
			<xsl:copy-of select="id" />
			<xsl:copy-of select="ontologyId" />
			<xsl:copy-of select="internalVersionNumber" />
			<xsl:copy-of select="versionNumber" />
			<xsl:copy-of select="versionStatus" />
			<xsl:copy-of select="contactName" />
			<xsl:copy-of select="contactEmail" />
		</ontology>
	</xsl:template>

</xsl:stylesheet>


