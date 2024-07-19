ALTER TABLE `login`.`user` 
ADD COLUMN `message_count` BIGINT NOT NULL DEFAULT 0 AFTER `username`,
CHANGE COLUMN `mfa_enabled` `mfa_enabled` BIT(1) NOT NULL DEFAULT b'0' ;