CREATE DATABASE `github_demo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `github_demo`;

DROP TABLE IF EXISTS `demo_user`;

CREATE TABLE `demo_user` (
  `id` varchar(32) NOT NULL COMMENT '用户id',
  `name` varchar(16) NOT NULL COMMENT '用户名',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别',
  `age` int(3) unsigned DEFAULT NULL COMMENT '用户年龄',
  `gmt_create` timestamp NULL DEFAULT NULL COMMENT '记录创建时间',
  `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '记录最近修改时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `phone` varchar(11) NOT NULL COMMENT '电话号码',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

INSERT INTO `demo_user` (`id`, `name`, `gender`, `age`, `gmt_create`
	, `gmt_modified`, `deleted`, `phone`)
VALUES ('11111111111111111111111111111111', 'zzs001', 0, 17, '2021-04-28 11:25:18'
		, '2021-05-02 09:33:56', 0, '188******42'),
	('4480067e571040089cb6b821fe94085a', 'zzf001', 0, 18, '2021-05-02 09:33:56'
		, '2021-05-02 09:33:56', 0, '188******26'),
	('678c5d2daf0c43d4b97e0633c8fc255d', 'zzs003', 0, 18, '2021-04-28 11:25:18'
		, '2021-04-28 11:25:42', 0, '188******43'),
	('8b9eb24b87b34092a1c4bb1ef06f26be', 'zzs002', 0, 18, '2021-04-28 11:25:18'
		, '2021-04-28 11:25:42', 0, '188******41');