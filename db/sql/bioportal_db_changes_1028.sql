USE `bioportal`;

/*Table structure for table `ncbo_l_notification_type` */

DROP TABLE IF EXISTS `ncbo_l_notification_type`;

CREATE TABLE `ncbo_l_notification_type` (
  `id` int(11) NOT NULL,
  `type` varchar(255) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

INSERT INTO ncbo_l_notification_type VALUES(10, 'Notes');
INSERT INTO ncbo_l_notification_type VALUES(3, 'ALL_NOTIFICATION');

ALTER TABLE `ncbo_user_subscriptions` ADD COLUMN `notification_type` int(11) NOT NULL DEFAULT 10;
ALTER TABLE `ncbo_user_subscriptions` ADD CONSTRAINT `FK_ncbo_notification_type` FOREIGN KEY (`notification_type`) REFERENCES `ncbo_l_notification_type` (`id`);