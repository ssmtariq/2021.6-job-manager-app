package com.ssmtariq.jobmanager.service;

import static com.ssmtariq.jobmanager.constant.ApplicationConstants.BCC;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.CC;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.MESSAGE_BODY;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.SUBJECT;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.TO;
import static org.quartz.JobKey.jobKey;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.ssmtariq.jobmanager.model.JobDefinition;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements JobService{
	private final Scheduler scheduler;

	/**
	 * Create a job
	 * @param group String
	 * @param definition JobDefinition
	 * @return JobDefinition
	 */
	@Override
	public JobDefinition createJob(String group, JobDefinition definition) {
		definition.setGroup(group);
		JobDetail jobDetail = definition.buildJobDetail();
		Set<Trigger> triggersForJob = definition.buildTriggers();
		log.info("About to save job with key - {}", jobDetail.getKey());
		try {
			scheduler.scheduleJob(jobDetail, triggersForJob, false);
			log.info("Job with key - {} saved sucessfully", jobDetail.getKey());
		} catch (SchedulerException e) {
			log.error("Could not save job with key - {} due to error - {}", jobDetail.getKey(), e.getLocalizedMessage());
			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
		return definition;
	}

	/**
	 * Find an existing job
	 * @param group String
	 * @param name String
	 * @return JobDefinition
	 */
	@Override
	public Optional<JobDefinition> findJob(String group, String name) {
		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
			if(Objects.nonNull(jobDetail))
				return Optional.of(
						JobDefinition.buildDefinition(jobDetail,
								scheduler.getTriggersOfJob(jobKey(name, group))));
		} catch (SchedulerException e) {
			log.error("Could not find job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
		log.warn("Could not find job with key - {}.{}", group, name);
		return Optional.empty();
	}

	/**
	 * Update a job
	 * @param group String
	 * @param name String
	 * @param definition JobDefinition
	 */
	@Override
	public void updateJob(String group, String name, JobDefinition definition) {
		try {
			JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
			if(Objects.nonNull(oldJobDetail)) {
				JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
				jobDataMap.put(SUBJECT, definition.getSubject());
				jobDataMap.put(MESSAGE_BODY, definition.getMessageBody());
				jobDataMap.put(TO, definition.getTo());
				jobDataMap.put(CC, definition.getCc());
				jobDataMap.put(BCC, definition.getBcc());
				JobBuilder jb = oldJobDetail.getJobBuilder();
				JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
				scheduler.addJob(newJobDetail, true);
				log.info("Updated job with key - {}", newJobDetail.getKey());
				return;
			}
			log.warn("Could not find job with key - {}.{} to update", group, name);
		} catch (SchedulerException e) {
			log.error("Could not find job with key - {}.{} to update due to error - {}", group, name, e.getLocalizedMessage());
		}
	}

	/**
	 * Delete a job
	 * @param group String
	 * @param name String
	 */
	@Override
	public void deleteJob(String group, String name) {
		try {
			scheduler.deleteJob(jobKey(name, group));
			log.info("Deleted job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not delete job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}

	/**
	 * Pause a Job
	 * @param group String
	 * @param name String
	 */
	@Override
	public void pauseJob(String group, String name) {
		try {
			scheduler.pauseJob(jobKey(name, group));
			log.info("Paused job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not pause job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}

	/**
	 * Resume a Job
	 * @param group String
	 * @param name String
	 */
	@Override
	public void resumeJob(String group, String name) {
		try {
			scheduler.resumeJob(jobKey(name, group));
			log.info("Resumed job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not resume job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}
}
