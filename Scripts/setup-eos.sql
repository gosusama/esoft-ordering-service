CREATE DATABASE IF NOT EXISTS `eos`;
USE `eos`;

DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `authorities`;
DROP TABLE IF EXISTS `users`;

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(68) NOT NULL,
  `enabled` tinyint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Inserting data for table `users`
--

INSERT INTO `users` VALUES (1, 'admin', '{noop}admin', 1), (2, 'customer', '{noop}customer', 1), (3, 'customer1', '{noop}customer1', 1);


--
-- Table structure for table `authorities`
--

CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `authorities_idx` (`username`, `authority`),
  CONSTRAINT `authorities_ibfk` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Inserting data for table `authorities`
--

INSERT INTO `authorities` VALUES ('admin','ROLE_CUSTOMER'), ('admin','ROLE_ADMIN'), ('customer','ROLE_CUSTOMER'), ('customer1','ROLE_CUSTOMER');


--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(60) NOT NULL UNIQUE,
  `category` varchar(14) NOT NULL,
  `quantity` int NOT NULL,
  `service_name` varchar(13) NOT NULL,
  `amount` decimal(10, 2),
  `description` text,
  `note` text,
  `create_uid` int,
  `create_date` datetime,
  `write_date` datetime,
  PRIMARY KEY (`id`),
  KEY `fk_users_idx` (`create_uid`),
  CONSTRAINT `fk_users` FOREIGN KEY (`create_uid`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;


INSERT INTO orders(id, code, category, quantity, service_name, amount, create_uid, create_date) VALUES 
  (1, '1708867969268_2', 'LUXURY', 1, 'PHOTO_EDITING', 150.00, 1, NOW()), 
  (2, '1708868200717_3', 'SUPER_LUXURY', 2, 'VIDEO_EDITING', 150.50, 2, NOW()), 
  (3, '1708868289164_4', 'SUPREME_LUXURY', 3, 'VIDEO_EDITING', 200.00, 3, NOW())