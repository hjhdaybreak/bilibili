SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Table structure for t_video_view
-- ----------------------------
DROP TABLE IF EXISTS `t_video_view`;
CREATE TABLE `t_video_view`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `videoId`    bigint NOT NULL COMMENT '视频id',
    `userId`     bigint                                                  DEFAULT NULL COMMENT '用户id',
    `clientId`   varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '客户端id',
    `ip`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL COMMENT 'ip',
    `createTime` datetime                                                DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb3 COMMENT ='视频观看记录表';

SET FOREIGN_KEY_CHECKS = 1;
