<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" omit-xml-declaration="no" indent="no" />

	<xsl:template match="/">
		<success>
			<xsl:apply-templates />
		</success>
	</xsl:template>

	<xsl:template match="data">
		<data>
			<list>
				<xsl:apply-templates />
			</list>
		</data>
	</xsl:template>

	<xsl:template match="ontologyBean">
		<ontologyBean>
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
			<xsl:copy-of select="groupIds" />
			<xsl:copy-of select="isFoundry" />
			<xsl:copy-of select="dateCreated" />
		</ontologyBean>
	</xsl:template>

	<xsl:template match="accessedResource">
		<accessedResource>
			<xsl:value-of select="." />
		</accessedResource>
	</xsl:template>

	<xsl:template match="accessDate">
		<accessDate>
			<xsl:value-of select="." />
		</accessDate>
	</xsl:template>
	
	<xsl:template match="sessionId">
		<sessionId>
			<xsl:value-of select="." />
		</sessionId>
	</xsl:template>
</xsl:stylesheet>
