USE `bioportal`;

DROP TABLE IF EXISTS `ncbo_l_usage_event_type`;

CREATE TABLE `ncbo_l_usage_event_type` (
  `id` int(11) NOT NULL,
  `event_name` varchar(64) NOT NULL,
  `event_description` varchar(256) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1000, 'show_all_ontologies', 'Show all ontologies');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1001, 'show_ontology', 'Show ontology');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1002, 'show_virtual_ontology', 'Show virtual ontology');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1003, 'show_virtual_concept', 'Show virtual concept');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1004, 'visualize_ontology', 'Visualize Ontology');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1005, 'visualize_concept_direct', 'Visualize Concept Directly');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1006, 'visualize_concept_browse', 'Visualize Concept By Browsing');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1007, 'jump_to_search', 'Jump to Search');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1008, 'jump_to_nav', 'Jump to Navigation');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1009, 'tab_details', 'UI Tab Details');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1010, 'tab_visualization', 'UI Tab Visualization');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1011, 'tab_notes', 'UI Tab Notes');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1012, 'tab_mappings', 'UI Tab Mappings');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1013, 'tab_resource_index', 'UI Tab Resource Index');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1014, 'tab_metadata', 'UI Tab Metadata');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1015, 'tab_views', 'UI Tab Views');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1016, 'tab_projects', 'UI Tab Projects');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1017, 'tab_reviews', 'UI Tab Reviews');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1018, 'tab_metrics', 'UI Tab Metrics');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) value (1019, 'tab_widgets', 'UI Tab Widgets'); 

DROP TABLE IF EXISTS `ncbo_usage_log`;

CREATE TABLE `ncbo_usage_log` (
  `id` int(11) NOT NULL auto_increment,
  `application_id` varchar(64) default NULL,
  `event_type` int(11) NOT NULL,
  `request_url` varchar(256) default NULL,
  `http_method` varchar(8) default NULL,
  `user_id` int(11) default NULL,
  `ontology_version_id` int(11) default NULL,
  `ontology_id` int(11) default NULL,
  `ontology_name` varchar(128) default NULL,
  `concept_id` varchar(512) default NULL,
  `concept_name` varchar(512) default NULL,
  `search_query` varchar(128) default NULL,
  `search_parameters` varchar(64) default NULL,
  `num_search_results` int(11) default NULL,
  `date_accessed` datetime DEFAULT CURRENT_TIMESTAMP,  
  PRIMARY KEY  (`id`),
  KEY `idx_application_id` (`application_id`),
  KEY `idx_request_url` (`request_url`),
  KEY `idx_ontology_name` (`ontology_name`),
  KEY `idx_concept_id` (`concept_id`),
  KEY `idx_concept_name` (`concept_name`),
  KEY `idx_search_query` (`search_query`),
  KEY `idx_search_parameters` (`search_parameters`),
  KEY `FK_ncbo_usage_log_event_type` (`event_type`),
  CONSTRAINT `FK_ncbo_usage_log_event_type` FOREIGN KEY (`event_type`) REFERENCES `ncbo_l_usage_event_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;





