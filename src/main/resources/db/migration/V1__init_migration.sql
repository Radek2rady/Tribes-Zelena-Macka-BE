CREATE SCHEMA IF NOT EXISTS tribes;

CREATE TABLE `users`
(
    `id`       bigint NOT NULL AUTO_INCREMENT,
    `avatar`   varchar(255) DEFAULT NULL,
    `email`    varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `points`   int          DEFAULT NULL,
    `username` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
);

CREATE TABLE `kingdoms`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `kingdom_name` varchar(255) DEFAULT NULL,
    `user_id`      bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKog4q04pcflen9e8tb9yt9w6lx` (`user_id`),
    CONSTRAINT `FKog4q04pcflen9e8tb9yt9w6lx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `buildings`
(
    `dtype`       varchar(31) NOT NULL,
    `id`          bigint      NOT NULL AUTO_INCREMENT,
    `finished_at` bigint       DEFAULT NULL,
    `hp`          int          DEFAULT NULL,
    `level`       int          DEFAULT NULL,
    `started_at`  bigint       DEFAULT NULL,
    `type`        varchar(255) DEFAULT NULL,
    `kingdom_id`  bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK4tl05f1r1yrihfobph4vd6s0k` (`kingdom_id`),
    CONSTRAINT `FK4tl05f1r1yrihfobph4vd6s0k` FOREIGN KEY (`kingdom_id`) REFERENCES `kingdoms` (`id`)
);

CREATE TABLE `chat_message`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `message`    varchar(255) DEFAULT NULL,
    `user_id`    bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKd3oo2bhroe16913kh5pwtgk3h` (`user_id`),
    CONSTRAINT `FKd3oo2bhroe16913kh5pwtgk3h` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
);

CREATE TABLE `resources`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `amount`     int          DEFAULT NULL,
    `generation` int          DEFAULT NULL,
    `type`       varchar(255) DEFAULT NULL,
    `updated_at` bigint       DEFAULT NULL,
    `kingdom_id` bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKgmtvv8x2kd3hl99u6a5t65ajr` (`kingdom_id`),
    CONSTRAINT `FKgmtvv8x2kd3hl99u6a5t65ajr` FOREIGN KEY (`kingdom_id`) REFERENCES `kingdoms` (`id`)
);

CREATE TABLE `troops`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `attack`      int    NOT NULL,
    `defence`     int    NOT NULL,
    `finished_at` bigint NOT NULL,
    `hp`          int    NOT NULL,
    `level`       int    NOT NULL,
    `started_at`  bigint NOT NULL,
    `academy_id`  bigint DEFAULT NULL,
    `kingdom_id`  bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKowpajap6qd1ds2rwc14nt992x` (`academy_id`),
    KEY `FKql9vwvul1a9obmlyqiwbc67er` (`kingdom_id`),
    CONSTRAINT `FKowpajap6qd1ds2rwc14nt992x` FOREIGN KEY (`academy_id`) REFERENCES `buildings` (`id`),
    CONSTRAINT `FKql9vwvul1a9obmlyqiwbc67er` FOREIGN KEY (`kingdom_id`) REFERENCES `kingdoms` (`id`)
);
