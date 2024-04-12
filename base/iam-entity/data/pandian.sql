-- --------------------------------------------------------
-- 主机:                           docker_db
-- 服务器版本:                        5.7.22-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出  表 pandian.i_task_log 结构
DROP TABLE IF EXISTS `i_task_log`;
CREATE TABLE IF NOT EXISTS `i_task_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL COMMENT '用户',
  `batchCode` varchar(50) DEFAULT NULL COMMENT '批次Code',
  `assetCode` varchar(50) DEFAULT NULL COMMENT '资产Code',
  `status` int(11) DEFAULT NULL COMMENT '盘点结果（五种）',
  `remark` varchar(800) DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=utf8 COMMENT='盘点日志表';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_annex 结构
DROP TABLE IF EXISTS `s_annex`;
CREATE TABLE IF NOT EXISTS `s_annex` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT '0',
  `ext` varchar(50) NOT NULL DEFAULT '0',
  `size` int(11) NOT NULL DEFAULT '0',
  `checkCode` varchar(50) NOT NULL DEFAULT '0',
  `imgWidth` int(11) NOT NULL DEFAULT '0',
  `imgHeight` int(11) NOT NULL DEFAULT '0',
  `url` varchar(50) NOT NULL DEFAULT '0',
  `createby_id` int(11) NOT NULL DEFAULT '0',
  `createby_name` varchar(50) NOT NULL DEFAULT '0',
  `errorMsg` varchar(50) NOT NULL DEFAULT '0',
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8 COMMENT='系统附件表';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_dept 结构
DROP TABLE IF EXISTS `s_dept`;
CREATE TABLE IF NOT EXISTS `s_dept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) DEFAULT NULL COMMENT '部门编码',
  `name` varchar(45) DEFAULT NULL COMMENT '部门名称',
  `updateAt` datetime DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8 COMMENT='资产保管部门';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_dept_user 结构
DROP TABLE IF EXISTS `s_dept_user`;
CREATE TABLE IF NOT EXISTS `s_dept_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_code` varchar(50) DEFAULT NULL,
  `loginName` varchar(50) DEFAULT NULL,
  `isDisabled` bit(1) DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8 COMMENT='部门用户关系表';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_group 结构
DROP TABLE IF EXISTS `s_group`;
CREATE TABLE IF NOT EXISTS `s_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='班组';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_group_user 结构
DROP TABLE IF EXISTS `s_group_user`;
CREATE TABLE IF NOT EXISTS `s_group_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(50) DEFAULT NULL,
  `loginName` varchar(50) DEFAULT NULL,
  `isDisabled` bit(1) DEFAULT NULL COMMENT '启用状态',
  `updateAt` datetime DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 COMMENT='班组用户关系表';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_log 结构
DROP TABLE IF EXISTS `s_log`;
CREATE TABLE IF NOT EXISTS `s_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msg` varchar(50) NOT NULL DEFAULT '0',
  `creatAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createBy` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL,
  `clientIp` varchar(50) NOT NULL,
  `module` varchar(50) NOT NULL,
  `remark` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='系统日志表';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_login_user 结构
DROP TABLE IF EXISTS `s_login_user`;
CREATE TABLE IF NOT EXISTS `s_login_user` (
  `id` int(11) NOT NULL,
  `loginName` varchar(50) NOT NULL DEFAULT '0',
  `password` varchar(50) NOT NULL DEFAULT '0',
  `createAt` datetime DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  `lastLoginAt` datetime DEFAULT NULL COMMENT '上次登录时间',
  `errorCount` int(11) NOT NULL DEFAULT '0' COMMENT '连续错误次数',
  `forget_password` bit(1) NOT NULL DEFAULT b'0' COMMENT '请求重置密码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `loginName` (`loginName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='登录信息';

-- 数据导出被取消选择。
-- 导出  表 pandian.s_user 结构
DROP TABLE IF EXISTS `s_user`;
CREATE TABLE IF NOT EXISTS `s_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL COMMENT '姓名',
  `loginName` varchar(45) DEFAULT NULL COMMENT '用户名',
  `code` varchar(45) DEFAULT NULL COMMENT '员工号',
  `mobile` varchar(45) DEFAULT NULL COMMENT '手机号',
  `groups` varchar(50) DEFAULT '0' COMMENT '所属的班组code',
  `depts` varchar(50) DEFAULT '0' COMMENT '所属的资产保管部门code',
  `LiRunZhongXin` varchar(45) DEFAULT NULL COMMENT '利润中心',
  `WeiHuGongChang` varchar(45) DEFAULT NULL COMMENT '维护工厂',
  `isChecker` bit(1) DEFAULT NULL COMMENT '是否是盘点员',
  `isManager` bit(1) DEFAULT NULL COMMENT '是否是负责人',
  `isLeader` bit(1) DEFAULT NULL COMMENT '是否是领导',
  `isAdmin` bit(1) DEFAULT NULL,
  `isDisabled` bit(1) DEFAULT NULL COMMENT '启用状态',
  `updateAt` datetime DEFAULT NULL COMMENT '最后更新时间',
  `createAt` datetime DEFAULT NULL,
  `token` varchar(50) DEFAULT NULL COMMENT '用于Api交互',
  PRIMARY KEY (`id`),
  UNIQUE KEY `loginName` (`loginName`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COMMENT='用户';

-- 数据导出被取消选择。
-- 导出  表 pandian.u_batch 结构
DROP TABLE IF EXISTS `u_batch`;
CREATE TABLE IF NOT EXISTS `u_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) DEFAULT NULL COMMENT '批次号',
  `corpCode` varchar(45) DEFAULT NULL COMMENT '公司代码',
  `name` varchar(45) DEFAULT NULL COMMENT '任务名称',
  `startAt` datetime DEFAULT NULL COMMENT '盘点开始时间',
  `endAt` datetime DEFAULT NULL COMMENT '盘点结束时间',
  `ChuangJianShiJian` datetime DEFAULT NULL COMMENT 'Excel列中的创建时间',
  `creator` varchar(45) DEFAULT NULL COMMENT '创建者',
  `status` varchar(45) DEFAULT NULL COMMENT '批次状态： 01-创建，02-修改， 03-冻结， 04-关闭，05-打开',
  `isPublished` bit(1) DEFAULT NULL COMMENT '是否发布',
  `createAt` datetime DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='盘点批交表(用户上传)';

-- 数据导出被取消选择。
-- 导出  表 pandian.u_dict_gongchang 结构
DROP TABLE IF EXISTS `u_dict_gongchang`;
CREATE TABLE IF NOT EXISTS `u_dict_gongchang` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` char(50) NOT NULL COMMENT '维护工厂',
  `name` varchar(50) DEFAULT NULL COMMENT '维护工厂描述',
  `createAt` datetime DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  PRIMARY KEY (`code`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8 COMMENT='维护工厂';

-- 数据导出被取消选择。
-- 导出  表 pandian.u_dict_lirun 结构
DROP TABLE IF EXISTS `u_dict_lirun`;
CREATE TABLE IF NOT EXISTS `u_dict_lirun` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL COMMENT '利润中心',
  `name` varchar(50) DEFAULT NULL COMMENT '利润中心描述',
  `createAt` datetime DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  PRIMARY KEY (`code`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8 COMMENT='利润中心';

-- 数据导出被取消选择。
-- 导出  表 pandian.u_shebei 结构
DROP TABLE IF EXISTS `u_shebei`;
CREATE TABLE IF NOT EXISTS `u_shebei` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ZhuSheBeiBianMa` varchar(50) DEFAULT '0' COMMENT '主设备编码',
  `code` varchar(50) DEFAULT '0' COMMENT '子设备编码',
  `name` varchar(50) DEFAULT '0' COMMENT '子设备名称',
  `type` varchar(50) DEFAULT '0' COMMENT '子设备技术对象类型',
  `typeRemark` varchar(50) DEFAULT '0' COMMENT '子设备技术对象类型描述',
  `number` int(11) DEFAULT '0' COMMENT '子设备数量',
  `unit` varchar(50) DEFAULT '0' COMMENT '子设备单位',
  `TouYunRiQi` date DEFAULT NULL COMMENT '子设备投运日期',
  `location` varchar(50) DEFAULT '0' COMMENT '子设备存放地点',
  `ZhiZaoShang` varchar(50) DEFAULT '0' COMMENT '子设备制造商',
  `XingHao` varchar(50) DEFAULT '0' COMMENT '子设备型号',
  `BaoGuanRen` varchar(50) DEFAULT '0' COMMENT '子设备使用保管人',
  `createAt` datetime DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='主子设备';

-- 数据导出被取消选择。
-- 导出  表 pandian.u_task 结构
DROP TABLE IF EXISTS `u_task`;
CREATE TABLE IF NOT EXISTS `u_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batchCode` varchar(50) NOT NULL COMMENT '盘点计划批次号',
  `corpCode` varchar(50) DEFAULT NULL COMMENT '公司代码',
  `assetCode` varchar(50) DEFAULT NULL COMMENT '资产编码',
  `name` varchar(50) DEFAULT NULL COMMENT '资产名称',
  `category` varchar(50) DEFAULT NULL COMMENT '资产分类',
  `categoryRemark` varchar(50) DEFAULT NULL COMMENT '资产分类描述',
  `count` int(11) DEFAULT NULL COMMENT '数量',
  `unit` varchar(50) DEFAULT NULL COMMENT '单位',
  `BaoGuanBuMen` varchar(50) DEFAULT NULL COMMENT '资产保管部门',
  `BaoGuanBuMenMiaoShu` varchar(50) DEFAULT NULL COMMENT '资产保管部门描述',
  `BaoGuanRen` varchar(50) DEFAULT NULL COMMENT '保管人',
  `LiRunZhongXin` varchar(50) DEFAULT NULL COMMENT '利润中心',
  `status` varchar(50) DEFAULT NULL COMMENT '资产状态',
  `statusRemark` varchar(50) DEFAULT NULL COMMENT '资产状态描述',
  `ZhiZaoShang` varchar(50) DEFAULT NULL COMMENT '制造商',
  `location` varchar(50) DEFAULT NULL COMMENT '资产存放地点',
  `SuoShuZhanXian` varchar(50) DEFAULT NULL COMMENT '所属站线名称',
  `SheBeiBianMa` varchar(50) DEFAULT NULL COMMENT '设备编码',
  `groupCode` varchar(50) DEFAULT NULL COMMENT '班组',
  `groupRemark` varchar(50) DEFAULT NULL COMMENT '班组描述',
  `GuiGeXingHao` varchar(50) DEFAULT NULL COMMENT '规格型号',
  `ShiWuId` varchar(50) DEFAULT NULL COMMENT '实物Id',
  `WeiHuGongChang` varchar(50) DEFAULT NULL COMMENT '维护工厂',
  `GongNengWeiZhi` varchar(50) DEFAULT NULL COMMENT '功能位置',
  `GongNengWeiZhiMiaoShu` varchar(50) DEFAULT NULL COMMENT '功能位置描述',
  `createAt` datetime DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  `isDownloaded` bit(1) DEFAULT NULL COMMENT '是否已下载',
  `result_type_code` varchar(50) NOT NULL COMMENT '资产细类代码',
  `result_XiLieHao` varchar(50) DEFAULT NULL COMMENT '盘盈系列号',
  `result_checker_id` int(11) DEFAULT NULL COMMENT '盘点人',
  `result_checker_name` varchar(50) DEFAULT NULL COMMENT '盘点人Name',
  `result_At` datetime DEFAULT NULL COMMENT '盘点时间',
  `result_status` int(11) DEFAULT NULL COMMENT '盘点状态(五种)',
  `result_remark` varchar(50) DEFAULT NULL COMMENT '盘点备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=611 DEFAULT CHARSET=utf8 COMMENT='盘点任务表(用户上传)';

-- 数据导出被取消选择。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
