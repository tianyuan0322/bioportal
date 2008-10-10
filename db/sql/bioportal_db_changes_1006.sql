USE `bioportal`;

ALTER TABLE ncbo_ontology_version_metadata ADD
	COLUMN `synonym_slot` varchar(500) character set utf8 collate utf8_bin default NULL;

ALTER TABLE ncbo_ontology_version_metadata ADD
	COLUMN `preferred_name_slot` varchar(500) character set utf8 collate utf8_bin default NULL;

ALTER TABLE ncbo_ontology_version_metadata AUTO_INCREMENT = 1000;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = 'http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Synonym', 
	preferred_name_slot = 'http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Preferred_Name'
WHERE
	ontology_version_id = 13578;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = 'http://ccdb.ucsd.edu/SAO/1.2#synonym' 
WHERE
	ontology_version_id = 14391;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = 'Synonym_Name', 
	preferred_name_slot = 'Preferred_Name'
WHERE
	ontology_version_id = 21275;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = 'http://ccdb.ucsd.edu/SAO/1.2#synonym' 
WHERE
	ontology_version_id = 28096;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = 'http://revelation.wustl.edu/NPO.owl#synonym' 
WHERE
	ontology_version_id = 29531;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = 'http://bioontology.org/projects/ontologies/birnlex#synonyms' 
WHERE
	ontology_version_id = 29684;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = 'http://www.bootstrep.eu/ontology/GRO#synonym' 
WHERE
	ontology_version_id = 38291;

UPDATE ncbo_ontology_version_metadata 
SET
	synonym_slot = ':SYNONYMS' 
WHERE
	ontology_version_id = 38563;

UPDATE ncbo_ontology_version_metadata 
SET
	preferred_name_slot = 'http://epoch.stanford.edu/ProtocolOntology.owl#label'
WHERE
	ontology_version_id = 8056;

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
	ovm.target_terminologies,
	ovm.is_foundry,
	ovm.synonym_slot,
	ovm.preferred_name_slot
FROM
	ncbo_ontology o 
	INNER JOIN ncbo_ontology_version ov on o.id = ov.ontology_id
	INNER JOIN ncbo_ontology_version_metadata ovm ON ov.id = ovm.ontology_version_id;
