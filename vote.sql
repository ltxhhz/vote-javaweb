/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : vote

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 05/06/2022 12:45:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for history
-- ----------------------------
DROP TABLE IF EXISTS `history`;
CREATE TABLE `history`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `account` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `choice` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`, `account`) USING BTREE,
  INDEX `uuid`(`uuid`) USING BTREE,
  CONSTRAINT `history_ibfk_1` FOREIGN KEY (`uuid`) REFERENCES `list` (`uuid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for list
-- ----------------------------
DROP TABLE IF EXISTS `list`;
CREATE TABLE `list`  (
  `uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `account` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `start` timestamp(0) NOT NULL,
  `end` timestamp(0) NOT NULL,
  `single` tinyint(1) UNSIGNED NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `optionsNum` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `min` int(10) UNSIGNED NULL DEFAULT 0,
  `max` int(10) UNSIGNED NULL DEFAULT 0,
  `everyday` tinyint(1) NOT NULL DEFAULT 0,
  `hideResult` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`uuid`, `account`) USING BTREE,
  INDEX `uuid`(`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for num
-- ----------------------------
DROP TABLE IF EXISTS `num`;
CREATE TABLE `num`  (
  `uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `visit` int(11) NOT NULL DEFAULT 0,
  `part` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`uuid`) USING BTREE,
  CONSTRAINT `fk` FOREIGN KEY (`uuid`) REFERENCES `list` (`uuid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for options
-- ----------------------------
DROP TABLE IF EXISTS `options`;
CREATE TABLE `options`  (
  `uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `optionId` char(39) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `image` char(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `num` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`optionId`, `uuid`) USING BTREE,
  INDEX `uuid`(`uuid`) USING BTREE,
  CONSTRAINT `options_ibfk_1` FOREIGN KEY (`uuid`) REFERENCES `list` (`uuid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `account` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `permission` int(1) NOT NULL,
  `registerTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`, `account`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
