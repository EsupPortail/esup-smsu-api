create table account (ACC_ID integer not null auto_increment, ACC_LABEL varchar(32) not null, ACC_QUOTA bigint, ACC_CONSUMED_SMS bigint not null, ACC_BROKER varchar(255), primary key (ACC_ID)) ENGINE=InnoDB;
create table application (APP_ID integer not null auto_increment, APP_NAME varchar(32) not null, APP_CERTIFCATE blob, APP_QUOTA bigint not null, APP_CONSUMED_SMS bigint not null, ACC_ID integer not null, INS_ID integer not null, primary key (APP_ID)) ENGINE=InnoDB;
create table blacklist (BLA_ID integer not null auto_increment, BLA_DATE datetime not null, BLA_PHONE varchar(255) not null, APP_ID integer not null, primary key (BLA_ID)) ENGINE=InnoDB;
create table institution (INS_ID integer not null auto_increment, INS_LABEL varchar(32) not null, primary key (INS_ID)) ENGINE=InnoDB;
create table sms (SMS_ID integer not null auto_increment, SMS_INITIAL_ID integer, SMS_SENDER_ID integer, BROKER_SMS_ID varchar(255), SMS_STATE varchar(32) not null, SMS_DATE datetime not null, SMS_ACK_DATE datetime, SMS_PHONE varchar(255), APP_ID integer not null, ACC_ID integer not null, primary key (SMS_ID)) ENGINE=InnoDB;
create table statistic (APP_ID integer not null, ACC_ID integer not null, STAT_MONTH date not null, STAT_NB_SMS bigint not null, STAT_NB_SMS_IN_ERROR bigint not null, primary key (APP_ID, ACC_ID, STAT_MONTH)) ENGINE=InnoDB;
alter table account add constraint UK_ACC_LABEL unique (ACC_LABEL);
alter table application add constraint UK_APP_NAME unique (APP_NAME);
alter table blacklist add constraint UK_BLA_PHONE unique (BLA_PHONE);
alter table institution add constraint UK_INS_LABEL unique (INS_LABEL);
create index check_already_sent on sms (SMS_INITIAL_ID, SMS_PHONE);
alter table application add constraint FK_app_acc foreign key (ACC_ID) references account (ACC_ID);
alter table application add constraint FK_app_institution foreign key (INS_ID) references institution (INS_ID);
alter table blacklist add constraint FK_blacklist_app foreign key (APP_ID) references application (APP_ID);
alter table sms add constraint FK_sms_app foreign key (APP_ID) references application (APP_ID);
alter table sms add constraint FK_sms_acc foreign key (ACC_ID) references account (ACC_ID);
alter table statistic add constraint FK_statistic_app foreign key (APP_ID) references application (APP_ID);
alter table statistic add constraint FK_statistic_acc foreign key (ACC_ID) references account (ACC_ID);
