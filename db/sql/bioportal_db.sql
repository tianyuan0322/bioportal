DROP TABLE IF EXISTS ncbo_ontology_metadata
;
DROP TABLE IF EXISTS ncbo_ontology_category
;
DROP TABLE IF EXISTS ncbo_ontology_additional_metadata
;
DROP TABLE IF EXISTS ncbo_ontology
;
DROP TABLE IF EXISTS ncbo_l_category
;
DROP TABLE IF EXISTS ncbo_l_additional_metadata
;



CREATE TABLE ncbo_ontology_metadata
(
	id INTEGER NOT NULL,
	ontology_id INTEGER NOT NULL,
	display_label VARCHAR(128) NOT NULL,
	format VARCHAR(50) NOT NULL,
	contact_name VARCHAR(128),
	contact_email VARCHAR(128),
	homepage VARCHAR(254),
	documentation VARCHAR(254),
	publication VARCHAR(254),
	is_foundry BIT NOT NULL,
	PRIMARY KEY (id),
	KEY (ontology_id)
) 
;


CREATE TABLE ncbo_ontology_category
(
	id INTEGER NOT NULL,
	ontology_id INTEGER NOT NULL,
	category_id INTEGER NOT NULL,
	PRIMARY KEY (id),
	KEY (category_id),
	KEY (ontology_id)
) 
;


CREATE TABLE ncbo_ontology_additional_metadata
(
	id INTEGER NOT NULL,
	ontology_id INTEGER NOT NULL,
	additional_metadata_id INTEGER NOT NULL,
	additional_metadata_value VARCHAR(254),
	PRIMARY KEY (id),
	KEY (additional_metadata_id),
	KEY (ontology_id)
) 
;


CREATE TABLE ncbo_ontology
(
	id INTEGER NOT NULL,
	parent_id INTEGER,
	version VARCHAR(32),
	file_path VARCHAR(254),
	filename VARCHAR(254),
	is_current BIT NOT NULL,
	is_remote BIT NOT NULL,
	is_reviewed BIT NOT NULL DEFAULT 0,
	date_released DATETIME NOT NULL,
	PRIMARY KEY (id),
	KEY (parent_id)
) 
;


CREATE TABLE ncbo_l_category
(
	id INTEGER NOT NULL,
	name VARCHAR(128) NOT NULL,
	parent_category_id INTEGER,
	PRIMARY KEY (id),
	UNIQUE (name),
	KEY (parent_category_id)
) 
;


CREATE TABLE ncbo_l_additional_metadata
(
	id INTEGER NOT NULL,
	name VARCHAR(128) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (name)
) 
;






ALTER TABLE ncbo_ontology_metadata ADD CONSTRAINT FK_ncbo_ontology_metadata_ncbo_ontology 
	FOREIGN KEY (ontology_id) REFERENCES ncbo_ontology (id)
;

ALTER TABLE ncbo_ontology_category ADD CONSTRAINT FK_ncbo_ontology_category_ncbo_l_category 
	FOREIGN KEY (category_id) REFERENCES ncbo_l_category (id)
;

ALTER TABLE ncbo_ontology_category ADD CONSTRAINT FK_ncbo_ontology_category_ncbo_ontology 
	FOREIGN KEY (ontology_id) REFERENCES ncbo_ontology (id)
;

ALTER TABLE ncbo_ontology_additional_metadata ADD CONSTRAINT FK_ncbo_ontology_additional_metadata_ncbo_l_additional_metadata 
	FOREIGN KEY (additional_metadata_id) REFERENCES ncbo_l_additional_metadata (id)
;

ALTER TABLE ncbo_ontology_additional_metadata ADD CONSTRAINT FK_ncbo_ontology_additional_metadata_ncbo_ontology 
	FOREIGN KEY (ontology_id) REFERENCES ncbo_ontology (id)
;

ALTER TABLE ncbo_ontology ADD CONSTRAINT FK_ncbo_ontology_ncbo_ontology 
	FOREIGN KEY (parent_id) REFERENCES ncbo_ontology (id)
;

ALTER TABLE ncbo_l_category ADD CONSTRAINT FK_ncbo_l_category_ncbo_l_category 
	FOREIGN KEY (parent_category_id) REFERENCES ncbo_l_category (id)
;
