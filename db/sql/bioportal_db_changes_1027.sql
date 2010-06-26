USE `bioportal`;

DROP TABLE IF EXISTS `ncbo_user_subscriptions`;

CREATE TABLE `ncbo_user_subscriptions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ontology_id` varchar(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;