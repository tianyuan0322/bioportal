set @var_ontology_version_id := 15830; 

delete from ncbo_ontology_file where ontology_version_id = @var_ontology_version_id;
delete from ncbo_ontology_load_queue where ontology_version_id = @var_ontology_version_id;
