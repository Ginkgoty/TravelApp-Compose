/*
 Navicat Premium Data Transfer

 Source Server         : Aliyun
 Source Server Type    : PostgreSQL
 Source Server Version : 140003 (140003)
 Source Host           : 118.31.67.238:5433
 Source Catalog        : travelapp
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 140003 (140003)
 File Encoding         : 65001

 Date: 01/04/2023 23:23:36
*/


-- ----------------------------
-- Table structure for detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."detail";
CREATE TABLE "public"."detail" (
  "sid" int4 NOT NULL,
  "intro" text COLLATE "pg_catalog"."default" NOT NULL,
  "tel" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "consumption" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "traffic" text COLLATE "pg_catalog"."default" NOT NULL,
  "ticket" text COLLATE "pg_catalog"."default" NOT NULL,
  "openness" text COLLATE "pg_catalog"."default" NOT NULL,
  "pic1" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "pic2" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "pic3" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "location" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "lat" numeric NOT NULL,
  "lng" numeric NOT NULL
)
;
ALTER TABLE "public"."detail" OWNER TO "postgres";

-- ----------------------------
-- Table structure for favorregions
-- ----------------------------
DROP TABLE IF EXISTS "public"."favorregions";
CREATE TABLE "public"."favorregions" (
  "uname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "rid" int4 NOT NULL
)
;
ALTER TABLE "public"."favorregions" OWNER TO "postgres";

-- ----------------------------
-- Table structure for favorspots
-- ----------------------------
DROP TABLE IF EXISTS "public"."favorspots";
CREATE TABLE "public"."favorspots" (
  "uname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "sid" int4 NOT NULL
)
;
ALTER TABLE "public"."favorspots" OWNER TO "postgres";

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS "public"."note";
CREATE TABLE "public"."note" (
  "nid" int4 NOT NULL,
  "img" text COLLATE "pg_catalog"."default" NOT NULL,
  "title" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "intro" text COLLATE "pg_catalog"."default" NOT NULL,
  "rname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."note" OWNER TO "postgres";

-- ----------------------------
-- Table structure for note_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."note_detail";
CREATE TABLE "public"."note_detail" (
  "nid" int4 NOT NULL,
  "background" text COLLATE "pg_catalog"."default" NOT NULL,
  "uname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "time" date NOT NULL,
  "title" text COLLATE "pg_catalog"."default" NOT NULL,
  "content" json NOT NULL
)
;
ALTER TABLE "public"."note_detail" OWNER TO "postgres";

-- ----------------------------
-- Table structure for region
-- ----------------------------
DROP TABLE IF EXISTS "public"."region";
CREATE TABLE "public"."region" (
  "rid" int4 NOT NULL,
  "rname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "intro" text COLLATE "pg_catalog"."default" NOT NULL,
  "pic" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."region" OWNER TO "postgres";

-- ----------------------------
-- Table structure for spot
-- ----------------------------
DROP TABLE IF EXISTS "public"."spot";
CREATE TABLE "public"."spot" (
  "sid" int4 NOT NULL,
  "rid" int4 NOT NULL,
  "sname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "rname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "pic" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "intro" text COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."spot" OWNER TO "postgres";

-- ----------------------------
-- Table structure for traveler
-- ----------------------------
DROP TABLE IF EXISTS "public"."traveler";
CREATE TABLE "public"."traveler" (
  "uname" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "pwd" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."traveler" OWNER TO "postgres";

-- ----------------------------
-- Primary Key structure for table detail
-- ----------------------------
ALTER TABLE "public"."detail" ADD CONSTRAINT "景点详情_pkey" PRIMARY KEY ("sid");

-- ----------------------------
-- Primary Key structure for table favorregions
-- ----------------------------
ALTER TABLE "public"."favorregions" ADD CONSTRAINT "favoregion_pkey" PRIMARY KEY ("uname", "rid");

-- ----------------------------
-- Primary Key structure for table favorspots
-- ----------------------------
ALTER TABLE "public"."favorspots" ADD CONSTRAINT "favorite_pkey" PRIMARY KEY ("uname", "sid");

-- ----------------------------
-- Primary Key structure for table note
-- ----------------------------
ALTER TABLE "public"."note" ADD CONSTRAINT "note_pkey" PRIMARY KEY ("nid");

-- ----------------------------
-- Primary Key structure for table note_detail
-- ----------------------------
ALTER TABLE "public"."note_detail" ADD CONSTRAINT "note_detail_pkey" PRIMARY KEY ("nid");

-- ----------------------------
-- Primary Key structure for table region
-- ----------------------------
ALTER TABLE "public"."region" ADD CONSTRAINT "地区_pkey" PRIMARY KEY ("rid");

-- ----------------------------
-- Primary Key structure for table spot
-- ----------------------------
ALTER TABLE "public"."spot" ADD CONSTRAINT "s_pkey" PRIMARY KEY ("sid");

-- ----------------------------
-- Primary Key structure for table traveler
-- ----------------------------
ALTER TABLE "public"."traveler" ADD CONSTRAINT "user_pkey" PRIMARY KEY ("uname");

-- ----------------------------
-- Foreign Keys structure for table detail
-- ----------------------------
ALTER TABLE "public"."detail" ADD CONSTRAINT "detail_sid_fkey" FOREIGN KEY ("sid") REFERENCES "public"."spot" ("sid") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Keys structure for table favorregions
-- ----------------------------
ALTER TABLE "public"."favorregions" ADD CONSTRAINT "favorregions_rid_fkey" FOREIGN KEY ("rid") REFERENCES "public"."region" ("rid") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "public"."favorregions" ADD CONSTRAINT "favorregions_uname_fkey" FOREIGN KEY ("uname") REFERENCES "public"."traveler" ("uname") ON DELETE RESTRICT ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table favorspots
-- ----------------------------
ALTER TABLE "public"."favorspots" ADD CONSTRAINT "favorspots_sid_fkey" FOREIGN KEY ("sid") REFERENCES "public"."spot" ("sid") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "public"."favorspots" ADD CONSTRAINT "favorspots_uname_fkey" FOREIGN KEY ("uname") REFERENCES "public"."traveler" ("uname") ON DELETE RESTRICT ON UPDATE CASCADE;

-- ----------------------------
-- Foreign Keys structure for table note_detail
-- ----------------------------
ALTER TABLE "public"."note_detail" ADD CONSTRAINT "note_detail_nid_fkey" FOREIGN KEY ("nid") REFERENCES "public"."note" ("nid") ON DELETE RESTRICT ON UPDATE CASCADE;
