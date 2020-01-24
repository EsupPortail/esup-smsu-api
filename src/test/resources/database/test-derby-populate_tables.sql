insert into institution (INS_LABEL) values('esup-portail')
insert into account (ACC_LABEL, ACC_QUOTA, ACC_CONSUMED_SMS) values ('esup-smsu-api-account', 1000000, 0)
insert into application (APP_NAME, APP_CERTIFCATE, APP_QUOTA, APP_CONSUMED_SMS, ACC_ID, INS_ID) select 'esup-smsu-api-test-app', cast(X'01' as varchar(4096) for bit data), 1000000, 0, account.acc_id, institution.ins_id from account, institution
