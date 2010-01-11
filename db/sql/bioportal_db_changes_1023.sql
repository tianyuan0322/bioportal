USE `bioportal`;

ALTER TABLE ncbo_user ADD
	COLUMN `open_id` varchar(256) default NULL
	AFTER `username`;

