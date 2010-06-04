USE `bioportal`;

DROP TABLE IF EXISTS `ncbo_user_subscriptions`;

CREATE TABLE `ncbo_user_subscriptions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `ontology_id` varchar(11) DEFAULT 'NULL',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;