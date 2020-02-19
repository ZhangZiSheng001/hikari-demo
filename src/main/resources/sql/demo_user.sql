
CREATE DATABASE `github_demo` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

USE `github_demo`;

DROP TABLE IF EXISTS `demo_user`;

CREATE TABLE `demo_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `name` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户名',
  `age` int(3) unsigned DEFAULT NULL COMMENT '用户年龄',
  `gmt_create` datetime DEFAULT NULL COMMENT '记录创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '记录最近修改时间',
  `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `index_age` (`age`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


insert into `demo_user`(
  `id`, `name`, `age`, `gmt_create`, 
  `gmt_modified`, `deleted`
) 
values 
  (
    1, 'zzs001', 18, '2019-10-01 11:13:42', 
    '2019-11-01 11:13:50', '\0'
  ), 
  (
    2, 'zzs002', 18, '2019-11-23 00:00:00', 
    '2019-11-23 00:00:00', '\0'
  ), 
  (
    3, 'zzs003', 25, '2019-11-01 11:14:36', 
    '2019-11-03 00:00:00', '\0'
  ), 
  (
    4, 'zzf001', 26, '2019-11-04 11:14:51', 
    '2019-11-03 00:00:00', '\0'
  ), 
  (
    5, 'zzf002', 17, '2019-11-03 00:00:00', 
    '2019-11-03 00:00:00', '\0'
  );