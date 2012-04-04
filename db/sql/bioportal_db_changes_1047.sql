USE `bioportal`;

ALTER TABLE `ncbo_usage_log` MODIFY COLUMN `request_url` VARCHAR(2048) CHARACTER SET utf8 COLLATE utf8_bin;

ALTER TABLE `ncbo_usage_log` MODIFY COLUMN `search_query` VARCHAR(2048) CHARACTER SET utf8 COLLATE utf8_bin;

