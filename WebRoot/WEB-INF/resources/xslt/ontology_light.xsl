<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="no" indent="no" />

	<xsl:template match="/">
		<success>
			<data>
				<list>
					<xsl:apply-templates />
				</list>
			</data>
		</success>
	</xsl:template>

	<xsl:template match="ontology">
		<ontology>
			<xsl:copy-of select="id" />
			<xsl:copy-of select="ontologyId" />
			<xsl:copy-of select="displayLabel" />
			<xsl:copy-of select="description" />
			<xsl:copy-of select="abbreviation" />
			<xsl:copy-of select="format" />
			<xsl:copy-of select="internalVersionNumber" />
			<xsl:copy-of select="versionNumber" />
			<xsl:copy-of select="versionStatus" />
			<xsl:copy-of select="contactName" />
			<xsl:copy-of select="contactEmail" />
			<xsl:copy-of select="statusId" />
			<xsl:copy-of select="categoryIds" />
			<xsl:copy-of select="dateCreated" />
		</ontology>
	</xsl:template>
</xsl:stylesheet>
