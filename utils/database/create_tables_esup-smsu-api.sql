/*==============================================================*/
/* Nom de SGBD :  MySQL 5.0                                     */
/* Date de création :  22/06/2009 12:01:44                      */
/*==============================================================*/



/*==============================================================*/
/* Table : ACCOUNT                                              */
/*==============================================================*/
create table account
(
   ACC_ID               int not null auto_increment,
   ACC_LABEL            varchar(32) not null,
   ACC_QUOTA            bigint,
   ACC_CONSUMED_SMS     bigint not null,
   primary key (ACC_ID)
)
type = InnoDB;

alter table account
   add unique AK_ACCAK (ACC_LABEL);

/*==============================================================*/
/* Table : APPLICATION                                          */
/*==============================================================*/
create table application
(
   APP_ID               int not null auto_increment,
   INS_ID               int not null,
   ACC_ID               int not null,
   APP_NAME             varchar(32) not null,
   APP_CERTIFCATE       longblob,
   APP_QUOTA            bigint not null,
   APP_CONSUMED_SMS     bigint not null,
   primary key (APP_ID)
)
type = InnoDB;

alter table application
   add unique AK_APPAK (APP_NAME);

/*==============================================================*/
/* Table : BLACKLIST                                            */
/*==============================================================*/
create table blacklist
(
   BLA_ID               int not null auto_increment,
   APP_ID               int not null,
   BLA_DATE             datetime not null,
   BLA_PHONE            varchar(255) not null,
   primary key (BLA_ID)
)
type = InnoDB;

alter table blacklist
   add unique AK_BLAAK (BLA_PHONE);

/*==============================================================*/
/* Table : B_USER                                               */
/*==============================================================*/
create table b_user
(
   ID                   varchar(255) not null,
   DISP_NAME            varchar(255),
   ADMI                 numeric(1,0) not null,
   LANG                 varchar(255),
   primary key (ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : B_USER_ADMIN                                         */
/*==============================================================*/
create table b_user_admin
(
   ID                   varchar(255) not null,
   DISP_NAME            longtext,
   ADMI                 numeric(1,0) not null,
   LANG                 varchar(255),
   primary key (ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : B_VERS_MANA                                          */
/*==============================================================*/
create table b_vers_mana
(
   ID                   int not null auto_increment,
   VERS                 varchar(255),
   primary key (ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : B_VERS_MANA_ADMIN                                    */
/*==============================================================*/
create table b_vers_mana_admin
(
   ID                   int not null auto_increment,
   VERS                 varchar(255),
   primary key (ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : FONCTION                                             */
/*==============================================================*/
create table fonction
(
   FCT_ID               int not null auto_increment,
   FTC_NAME             varchar(32) not null,
   primary key (FCT_ID)
)
type = InnoDB;

alter table fonction
   add unique AK_FCTAK (FTC_NAME);

/*==============================================================*/
/* Table : INSTITUTION                                          */
/*==============================================================*/
create table institution
(
   INS_ID               int not null auto_increment,
   INS_LABEL            varchar(32) not null,
   primary key (INS_ID)
)
type = InnoDB;

alter table institution
   add unique AK_INSAK (INS_LABEL);

/*==============================================================*/
/* Table : ROLE                                                 */
/*==============================================================*/
create table role
(
   ROL_ID               int not null auto_increment,
   ROL_NAME             varchar(32) not null,
   primary key (ROL_ID)
)
type = InnoDB;

alter table role
   add unique AK_ROLAK (ROL_NAME);

/*==============================================================*/
/* Table : ROLE_COMPOSITION                                     */
/*==============================================================*/
create table role_composition
(
   ROL_ID               int not null,
   FCT_ID               int not null,
   primary key (ROL_ID, FCT_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : SMS                                                  */
/*==============================================================*/
create table sms
(
   SMS_ID               int not null auto_increment,
   APP_ID               int not null,
   ACC_ID               int not null,
   SMS_INITIAL_ID       int,
   SMS_SENDER_ID        int,
   BROKER_SMS_ID  	int,
   SMS_STATE            varchar(32) not null,
   SMS_DATE             timestamp not null default CURRENT_TIMESTAMP,
   SMS_PHONE            varchar(255),
   primary key (SMS_ID)
)
type = InnoDB;

/*==============================================================*/
/* Table : STATISTIC                                            */
/*==============================================================*/
create table statistic
(
   APP_ID               int not null,
   ACC_ID               int not null,
   STAT_MONTH           date not null,
   STAT_NB_SMS          bigint not null,
   STAT_NB_SMS_IN_ERROR bigint not null,
   primary key (APP_ID, ACC_ID, STAT_MONTH)
)
type = InnoDB;

/*==============================================================*/
/* Table : USER_BO_SMSU                                         */
/*==============================================================*/
create table user_bo_smsu
(
   USER_ID              int not null auto_increment,
   ROL_ID               int not null,
   USER_LOGIN           varchar(32) not null,
   primary key (USER_ID)
)
type = InnoDB;

alter table user_bo_smsu
   add unique AK_USERAK (USER_LOGIN);

alter table application add constraint FK_DEFAULT_ACCOUNT foreign key (ACC_ID)
      references account (ACC_ID) on delete restrict on update restrict;

alter table application add constraint FK_FROM_INSTITUTION foreign key (INS_ID)
      references institution (INS_ID) on delete restrict on update restrict;

alter table blacklist add constraint FK_ORIGIN foreign key (APP_ID)
      references application (APP_ID) on delete restrict on update restrict;

alter table role_composition add constraint FK_ROLE_COMPOSITION foreign key (ROL_ID)
      references role (ROL_ID) on delete restrict on update restrict;

alter table role_composition add constraint FK_ROLE_COMPOSITION2 foreign key (FCT_ID)
      references fonction (FCT_ID) on delete restrict on update restrict;

alter table sms add constraint FK_SOURCE_ACCOUNT foreign key (ACC_ID)
      references account (ACC_ID) on delete restrict on update restrict;

alter table sms add constraint FK_SOURCE_APPLICATION foreign key (APP_ID)
      references application (APP_ID) on delete restrict on update restrict;

alter table statistic add constraint FK_USAGE_ACCOUNT foreign key (ACC_ID)
      references account (ACC_ID) on delete restrict on update restrict;

alter table statistic add constraint FK_USAGE_APPLICATION foreign key (APP_ID)
      references application (APP_ID) on delete restrict on update restrict;

alter table user_bo_smsu add constraint FK_ROLE_USER foreign key (ROL_ID)
      references role (ROL_ID) on delete restrict on update restrict;

commit;