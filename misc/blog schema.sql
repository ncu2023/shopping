CREATE TABLE `articles` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(128),
  `subtitle` varchar(128),
  `img_url` varchar(255),
  `content` varchar(255),
  `author` varchar(32),
  `update_time` datetime
);
