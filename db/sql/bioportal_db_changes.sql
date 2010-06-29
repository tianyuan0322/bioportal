USE `bioportal`;

DROP TABLE IF EXISTS `ncbo_l_notification_type`;

CREATE TABLE `ncbo_l_notification_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `ncbo_l_notification_type` */

INSERT  INTO `ncbo_l_notification_type`(`id`,`type`) VALUES (1,'CREATE_NOTE_NOTIFICATION'),(2,'UPDATE_ONTOLOGY_NOTIFICATION');