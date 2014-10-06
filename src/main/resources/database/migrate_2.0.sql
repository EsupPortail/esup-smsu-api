alter table application modify APP_CERTIFCATE blob;

delete from QRTZ_CRON_TRIGGERS where TRIGGER_NAME = 'purgeStatisticTrigger';
delete from QRTZ_TRIGGERS where JOB_NAME = 'purgeStatisticJob';
delete from QRTZ_JOB_DETAILS where JOB_CLASS_NAME = 'org.esupportail.smsuapi.services.scheduler.job.PurgeStatisticJob';

alter table sms add SMS_ACK_DATE datetime;

-- sms with no initialId now gets automatically an initialId
-- (below is mysql only, sorry...)
SELECT @n := MAX(sms_initial_id) FROM sms;
UPDATE sms SET sms_initial_id = (@n := @n + 1) WHERE sms_initial_id IS NULL;
