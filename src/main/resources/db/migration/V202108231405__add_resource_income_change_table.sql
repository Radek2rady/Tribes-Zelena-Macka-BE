CREATE TABLE `resource_changes`
(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `amount` int DEFAULT NULL,
    `change_at` bigint DEFAULT NULL,
    `type` varchar(255) DEFAULT NULL,
    `resource_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKci2efq38gp62h843cjxk29tt3` (`resource_id`),
    CONSTRAINT `FKci2efq38gp62h843cjxk29tt3` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`id`)
);
