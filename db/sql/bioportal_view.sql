CREATE OR REPLACE VIEW `v_ncbo_ontology` AS
SELECT 
	ov.id,
	ov.ontology_id,				
	ov.parent_id,
	ov.user_id,
	ov.internal_version_number,
	ov.version_number,
	ov.version_status,
	ov.file_path,
	ov.is_current,
	ov.is_remote,
	ov.is_reviewed,
	ov.date_created,
	ov.date_released,
	om.display_label,
	om.format,
	om.contact_name,
	om.contact_email,
	om.homepage,
	om.documentation,
	om.publication,
	om.urn,
	om.coding_scheme,
	om.is_foundry
FROM
	ncbo_ontology_version ov 
	INNER JOIN ncbo_ontology_metadata om ON ov.id = om.ontology_version_id;


