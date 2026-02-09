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

DROP TABLE IF EXISTS `clan_members`;
CREATE TABLE `clan_members`
(
	`uuid`    CHAR(36)                             NOT NULL,
	`role`    ENUM ('LEADER', 'OFFICER', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
	`clan_id` INT                                  NOT NULL,
	PRIMARY KEY (`uuid`),
	FOREIGN KEY (`clan_id`) REFERENCES clans (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

DROP TABLE IF EXISTS `clan_home`;
CREATE TABLE `clan_home`
(
	`x`       INT NOT NULL,
	`y`       INT NOT NULL,
	`z`       INT NOT NULL,
	`clan_id` INT NOT NULL,
	PRIMARY KEY (`clan_id`),
	FOREIGN KEY (`clan_id`) REFERENCES clans (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
