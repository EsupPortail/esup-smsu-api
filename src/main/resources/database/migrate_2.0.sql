alter table application modify APP_CERTIFCATE blob;

delete from qrtz_cron_triggers where TRIGGER_NAME = 'purgeStatisticTrigger';
delete from qrtz_triggers where JOB_NAME = 'purgeStatisticJob';
delete from qrtz_job_details where JOB_CLASS_NAME = 'org.esupportail.smsuapi.services.scheduler.job.PurgeStatisticJob';

-- sms with no initialId now gets automatically an initialId
-- (below is mysql only, sorry...)
SELECT @n := MAX(sms_initial_id) FROM sms;
UPDATE sms SET sms_initial_id = (@n := @n + 1) WHERE sms_initial_id IS NULL;
