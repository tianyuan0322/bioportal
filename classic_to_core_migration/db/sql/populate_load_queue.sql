use bioportal;

insert into ncbo_ontology_load_queue (ontology_version_id, status_id)
select id, 1 from ncbo_ontology_version;
