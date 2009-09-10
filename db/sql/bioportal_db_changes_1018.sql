USE `bioportal`;

DROP TABLE ncbo_ontology_category;
DROP TABLE ncbo_ontology_additional_version_metadata;
DROP TABLE ncbo_l_additional_metadata;
DROP TABLE ncbo_l_category;

ALTER TABLE ncbo_ontology_load_queue DROP FOREIGN KEY FK_ncbo_ontology_load_queue_ontology_version_id;

ALTER TABLE ncbo_ontology_file DROP FOREIGN KEY FK_ncbo_ontology_file_ncbo_ontology_version;

ALTER TABLE ncbo_ontology_version_metadata DROP FOREIGN KEY FK_ncbo_ontology_version_metadata_ncbo_ontology_version;

ALTER TABLE ncbo_ontology_version DROP FOREIGN KEY FK_ncbo_ontology_ncbo_user;
ALTER TABLE ncbo_ontology_version DROP FOREIGN KEY FK_ncbo_ontology_version_ontology_id;
ALTER TABLE ncbo_ontology_version DROP FOREIGN KEY FK_ncbo_ontology_version_status_id;

DROP TABLE ncbo_ontology_version_metadata;
DROP TABLE ncbo_ontology_version;
DROP TABLE ncbo_ontology;

DROP VIEW v_ncbo_ontology;

DROP PROCEDURE IF EXISTS sp_remove_ontology_by_display_label;

ALTER TABLE ncbo_usage_log MODIFY request_parameters VARCHAR(2048);

