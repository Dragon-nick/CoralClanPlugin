DROP TABLE IF EXISTS `clans`;
CREATE TABLE `clans`
(
	`id`   INT          NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	`tag`  VARCHAR(10)  NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE (`name`, `tag`)
) ENGINE = InnoDB
	DEFAULT CHARSET = utf8mb4
	COLLATE = utf8mb4_unicode_ci;
