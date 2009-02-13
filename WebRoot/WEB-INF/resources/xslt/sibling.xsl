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

	<xsl:template match="org.ncbo.stanford.obs.bean.ContextBean">
		<SiblingBean>
			<xsl:copy-of select="localConceptId" />
			<xsl:copy-of select="level" />
		</SiblingBean>
	</xsl:template>
</xsl:stylesheet>
