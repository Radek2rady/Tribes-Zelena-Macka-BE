CREATE TABLE `confirmation_token`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `confirmed_at` datetime(6) DEFAULT NULL,
    `created_at`   datetime(6) NOT NULL,
    `expires_at`   datetime(6) NOT NULL,
    `token`        varchar(255) DEFAULT NULL,
    `user_id`      bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY            `FKah4p1rycwibwm6s9bsyeckq51` (`user_id`),
    CONSTRAINT `FKah4p1rycwibwm6s9bsyeckq51` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
)