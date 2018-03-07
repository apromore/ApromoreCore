ALTER TABLE `apromore`.`membership`
CHANGE COLUMN `password_question` `password_question` VARCHAR(50) NULL DEFAULT NULL ,
CHANGE COLUMN `password_answer` `password_answer` VARCHAR(50) NULL DEFAULT NULL ;