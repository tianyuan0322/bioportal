USE `bioportal`;

DROP TABLE IF EXISTS `ncbo_usage_log`;
DROP TABLE IF EXISTS `ncbo_l_usage_event_type`;

CREATE TABLE `ncbo_l_usage_event_type` (
  `id` INT(11) NOT NULL,
  `event_name` VARCHAR(64) NOT NULL,
  `event_description` VARCHAR(256) DEFAULT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=INNODB DEFAULT CHARSET=latin1;

CREATE UNIQUE INDEX `idx_event_name_unique` ON ncbo_l_usage_event_type(`event_name`);

INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1000, 'show_all_ontologies', 'Show all ontologies');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1001, 'show_ontology', 'Show ontology');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1002, 'show_virtual_ontology', 'Show virtual ontology');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1003, 'show_virtual_concept', 'Show virtual concept');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1004, 'visualize_ontology', 'Visualize Ontology');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1005, 'visualize_concept_direct', 'Visualize Concept Directly');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1006, 'visualize_concept_browse', 'Visualize Concept By Browsing');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1007, 'jump_to_search', 'Jump to Search');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1008, 'jump_to_nav', 'Jump to Navigation');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1009, 'tab_details', 'UI Tab Details');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1010, 'tab_visualization', 'UI Tab Visualization');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1011, 'tab_notes', 'UI Tab Notes');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1012, 'tab_mappings', 'UI Tab Mappings');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1013, 'tab_resource_index', 'UI Tab Resource Index');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1014, 'tab_metadata', 'UI Tab Metadata');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1015, 'tab_views', 'UI Tab Views');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1016, 'tab_projects', 'UI Tab Projects');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1017, 'tab_reviews', 'UI Tab Reviews');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1018, 'tab_metrics', 'UI Tab Metrics');
INSERT INTO ncbo_l_usage_event_type (id, event_name, event_description) VALUE (1019, 'tab_widgets', 'UI Tab Widgets'); 

CREATE TABLE `ncbo_usage_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `application_id` VARCHAR(64) DEFAULT NULL,
  `event_type` INT(11) NOT NULL,
  `request_url` VARCHAR(256) DEFAULT NULL,
  `http_method` VARCHAR(8) DEFAULT NULL,
  `user_id` INT(11) DEFAULT NULL,
  `session_id` VARCHAR(256) DEFAULT NULL,
  `ip_address` VARCHAR(32) DEFAULT NULL,
  `ontology_version_id` INT(11) DEFAULT NULL,
  `ontology_id` INT(11) DEFAULT NULL,
  `ontology_name` VARCHAR(128) DEFAULT NULL,
  `concept_id` VARCHAR(512) DEFAULT NULL,
  `concept_name` VARCHAR(512) DEFAULT NULL,
  `search_query` VARCHAR(128) DEFAULT NULL,
  `search_parameters` VARCHAR(64) DEFAULT NULL,
  `num_search_results` INT(11) DEFAULT NULL,
  `date_accessed` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
  PRIMARY KEY  (`id`),
  KEY `idx_application_id` (`application_id`),
  KEY `idx_request_url` (`request_url`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_ontology_name` (`ontology_name`),
  KEY `idx_concept_id` (`concept_id`),
  KEY `idx_concept_name` (`concept_name`),
  KEY `idx_search_query` (`search_query`),
  KEY `idx_search_parameters` (`search_parameters`),
  KEY `FK_ncbo_usage_log_event_type` (`event_type`),
  CONSTRAINT `FK_ncbo_usage_log_event_type` FOREIGN KEY (`event_type`) REFERENCES `ncbo_l_usage_event_type` (`id`)
) ENGINE=INNODB DEFAULT CHARSET=latin1;
