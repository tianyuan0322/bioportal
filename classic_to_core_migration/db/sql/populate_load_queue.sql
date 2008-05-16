use bioportal;

insert into ncbo_ontology_load_queue (ontology_version_id, status_id)
select id, 1 from ncbo_ontology_version where is_remote = 0;

update ncbo_ontology_version set status_id = 1;
