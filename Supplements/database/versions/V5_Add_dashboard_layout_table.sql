CREATE TABLE IF NOT EXISTS `dashboard_layout` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`userId` int(11) NOT NULL,
`logId` int(11) NOT NULL,
`layout` mediumtext,
PRIMARY KEY (`id`),
KEY `userId` (`userId`),
KEY `logId` (`logId`),
CONSTRAINT `dashboard_layout_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `dashboard_layout_ibfk_2` FOREIGN KEY (`logId`) REFERENCES `log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;