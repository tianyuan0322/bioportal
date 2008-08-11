CREATE OR REPLACE VIEW `v_ncbo_ontology` AS
SELECT 
	ov.id,
	ov.ontology_id,				
	ov.user_id,
	ov.internal_version_number,
	ov.version_number,
	ov.version_status,
	ov.file_path,
	ov.is_remote,
	ov.is_reviewed,
	ov.status_id,
	ov.date_created,
	ov.date_released,
	o.obo_foundry_id,
	o.is_manual,
	ovm.display_label,
	ovm.format,
	ovm.contact_name,
	ovm.contact_email,
	ovm.homepage,
	ovm.documentation,
	ovm.publication,
	ovm.urn,
	ovm.coding_scheme,
	ovm.is_foundry
FROM
	ncbo_ontology o 
	INNER JOIN ncbo_ontology_version ov on o.id = ov.ontology_id
	INNER JOIN ncbo_ontology_version_metadata ovm ON ov.id = ovm.ontology_version_id;
