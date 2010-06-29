USE `bioportal`;

DROP TABLE IF EXISTS `ncbo_user_subscriptions`;

CREATE TABLE `ncbo_user_subscriptions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ontology_id` varchar(25) CHARACTER SET utf8 NOT NULL,
  `notification_type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ncbo_notification_type` (`notification_type`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

/*Data for the table `ncbo_user_subscriptions` */

insert  into `ncbo_user_subscriptions`(`id`,`user_id`,`ontology_id`,`notification_type`) values (1,38578,'1083',2);