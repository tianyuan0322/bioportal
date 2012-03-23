USE `bioportal`;

ALTER TABLE `ncbo_usage_log` MODIFY COLUMN `request_url` VARCHAR(2048) CHARACTER SET utf8 COLLATE utf8_bin;
