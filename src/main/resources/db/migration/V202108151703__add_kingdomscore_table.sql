CREATE TABLE `kingdom_score`
(
    `id`              bigint NOT NULL AUTO_INCREMENT,
    `buildings_score` int    DEFAULT NULL,
    `resources_score` int    DEFAULT NULL,
    `total_score`     int    DEFAULT NULL,
    `troops_score`    int    DEFAULT NULL,
    `kingdom_id`      bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK6xvq7l6brj1pfqj3vvw8n56xf` (`kingdom_id`),
    CONSTRAINT `FK6xvq7l6brj1pfqj3vvw8n56xf` FOREIGN KEY (`kingdom_id`) REFERENCES `kingdoms` (`id`)
);