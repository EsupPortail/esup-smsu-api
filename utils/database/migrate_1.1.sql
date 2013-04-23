alter table sms modify SMS_PHONE varchar(255) NOT NULL;
alter table blacklist modify BLA_PHONE varchar(255) NOT NULL;

alter table sms drop SMS_SVC_ID;
alter table sms drop SMS_GRP_SENDER_ID;
