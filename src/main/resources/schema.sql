CREATE SCHEMA IF NOT EXISTS PATAUTH;

create table IF NOT EXISTS HEB_USER_DETAILS (
  USR_ID VARCHAR(7) not null primary key,
  PWD VARCHAR(8) not null,
  DISPLAY_NAME VARCHAR(7),
  FIRSTNAME VARCHAR(7),
  LASTNAME VARCHAR(7),
  HEBJOBCODE VARCHAR(7),
  HEBJOBDESC VARCHAR(7),
  DEPARTMENT_NUMBER VARCHAR(7),
  MAIL VARCHAR(7),
  ROLE VARCHAR(7),
  MOBILE VARCHAR(7),
  LOG_TS DATE,
  VENDORORGID VARCHAR(7),
  VENDORORGNAME VARCHAR(7),
  HEBGLLOCATION VARCHAR(7),
  BAT_SW CHAR NOT NULL,
  IP_TXT VARCHAR(30)
);

create table IF NOT EXISTS PATAUTH.USR_JTI (
  JWT_ID VARCHAR2 not null primary key,
  USR_ID VARCHAR(7),
  USR_NM VARCHAR2 not null,
  LOG_TS DATE,
  RFRSH_TS DATE,
  BAT_SW CHAR not null,
  IP_TXT VARCHAR(30)
);

create table APPL_NM
(
  APPL_ID INT primary key,
  APPL_NM varchar(20) not null,
  BUS_OWNER_DEPT varchar(30) not null
);

create table USR_ROLE
(
  USR_ROLE_CD int primary key,
  USR_ROLE_ABB varchar(6) not null,
  USR_ROLE_DES varchar(50) not null,
  CRE8_TS datetime,
  JOB_CD_XREF varchar(6),
  LST_UID varchar(8),
  LST_UPDT_TS datetime,
  APPL_ID int not null
);

create table USR_SEC_GRP
(
  USR_ID varchar(20) not null,
  USR_ROLE_CD int not null,
  CRE8_TS datetime not null,
  LST_UID varchar(8),
  LST_UPDT_TS datetime not null,
  primary key (USR_ID, USR_ROLE_CD)
);

create table USR_ROLE_JOB_CD
(
  USR_ROLE_CD int not null,
  JOB_CD varchar(6) not null,
  primary key (USR_ROLE_CD, JOB_CD)
);

create table IDM
(
  USR_ID varchar(20) not null
    primary key,
  FRST_NM varchar(50),
  LST_NM varchar(50),
  FRST_LOG_IN_TS datetime not null,
  LST_LOG_IN_TS datetime not null,
  EMAIL_ID varchar(80)
);

create table RESRC
(
  RESRC_ID int primary key,
  RESRC_NM varchar(30) not null,
  CRE8_TS datetime not null,
  LST_UID varchar(8) not null,
  LST_UPDT_TS datetime not null,
  RESRC_TYP_ID int,
  TOOL_TIP_SW varchar(1) not null,
  RESRC_DEFNTN varchar(255),
  SCREEN_NM varchar(50),
  APPL_ID int,
  PARNT_RESRC_ID int
);

create table SEC_GRP_RESRC
(
  RESRC_ID int not null,
  USR_ROLE_CD int not null,
  ACS_CD varchar(5) not null,
  EFF_DT datetime,
  EXPRN_DT datetime not null,
  CRE8_TS datetime not null,
  LST_UID varchar(8) not null,
  LST_UPDT_TS datetime not null
);

create table ACCESS_TYPE
(
  ACS_CD varchar(5) not null
    primary key,
  ACS_ABB varchar(6) not null,
  ACS_DES varchar(50) not null,
  CRE8_TS datetime not null,
  LST_UID varchar(8),
  LST_UPDT_TS datetime not null
);
