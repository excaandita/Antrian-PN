CREATE TABLE user ( 
    `id` INT(3) NOT NULL AUTO_INCREMENT , 
    `name` TEXT NULL , 
    `username` VARCHAR(255) NOT NULL , 
    `password` VARCHAR(255) NOT NULL , 
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , 
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , 
PRIMARY KEY (`id`), UNIQUE (`username`)) ENGINE = InnoDB;

CREATE TABLE court_room ( 
    `id` INT(3) NOT NULL AUTO_INCREMENT , 
    `name` VARCHAR(255) NOT NULL , 
    `description` TEXT NULL , 
    `status` ENUM('0','1') NOT NULL DEFAULT '1' , 
    `active` ENUM('0','1') NOT NULL DEFAULT '0' , 
    `file_sound` TEXT NULL , PRIMARY KEY (`id`), 
INDEX (`status`), INDEX (`active`)) ENGINE = InnoDB;

CREATE TABLE queue ( 
    `id` INT(3) NOT NULL AUTO_INCREMENT , 
    `date` DATE NOT NULL , 
    `queue_number` INT(3) NOT NULL , 
    `id_court_room` INT(3) NOT NULL , 
    `call_time` DATETIME NOT NULL , 
    `pick_up_time` DATETIME NOT NULL , 
    `status` ENUM('0','1') NOT NULL DEFAULT '0' , 
PRIMARY KEY (`id`), INDEX (`status`)) ENGINE = InnoDB;

CREATE TABLE video ( 
    `id` INT(3) NOT NULL AUTO_INCREMENT , 
    `name` VARCHAR(255) NOT NULL , 
    `path` TEXT NOT NULL , 
PRIMARY KEY (`id`)) ENGINE = InnoDB;

CREATE TABLE `running_text` ( 
    `id` INT(3) NOT NULL AUTO_INCREMENT , 
    `description` TEXT NOT NULL , 
    `status` ENUM('0','1') NOT NULL , 
PRIMARY KEY (`id`), INDEX (`status`)) ENGINE = InnoDB;