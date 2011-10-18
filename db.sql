CREATE TABLE IF NOT EXISTS `homes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `world` varchar(50) NOT NULL DEFAULT 'world',
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `rotX` float NOT NULL,
  `rotY` float NOT NULL,
  `group` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `savehomes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `world` varchar(50) NOT NULL DEFAULT 'world',
  `x` double NOT NULL,
  `y` double NOT NULL,
  `z` double NOT NULL,
  `rotX` float NOT NULL,
  `rotY` float NOT NULL,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`description`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;
