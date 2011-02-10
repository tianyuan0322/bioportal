USE `bioportal`;

ALTER TABLE `ncbo_user` ADD COLUMN `api_key` VARCHAR(36) CHARACTER SET utf8 COLLATE utf8_bin NULL
AFTER `username`;

UPDATE ncbo_user SET api_key = UUID();

ALTER TABLE `ncbo_user` MODIFY COLUMN `api_key` VARCHAR(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

CREATE UNIQUE INDEX `idx_api_key_unique` ON ncbo_user(`api_key`);

DROP TABLE IF EXISTS `ncbo_ontology_acl`;

CREATE TABLE `ncbo_ontology_acl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ontology_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `is_owner` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_ontology_id_user_id_unique` (`ontology_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
