package com.ssmtariq.jobmanager.model;

import static com.ssmtariq.jobmanager.constant.ApplicationConstants.CRON;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.FIRE_TIME;
import static java.time.ZoneId.systemDefault;
import static java.util.UUID.randomUUID;
import static org.quartz.CronExpression.isValidExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.springframework.util.StringUtils.isEmpty;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.quartz.JobDataMap;
import org.quartz.Trigger;

import lombok.Data;

@Data
public class TriggerDefinition {
    private String name;
    private String group;
    private LocalDateTime fireTime;
    private String cron;

    /**
     * Set TriggerDefinition name
     * @param name String
     * @return TriggerDefinition
     */
    public TriggerDefinition setName(final String name) {
        this.name = name;
        return this;
    }

    /**
     * Set TriggerDefinition group
     * @param group String
     * @return TriggerDefinition
     */
    public TriggerDefinition setGroup(final String group) {
        this.group = group;
        return this;
    }

    /**
     * Set TriggerDefinition fireTime
     * @param fireTime LocalDateTime
     * @return TriggerDefinition
     */
    public TriggerDefinition setFireTime(final LocalDateTime fireTime) {
        this.fireTime = fireTime;
        return this;
    }

    /**
     * Set TriggerDefinition cron
     * @param cron String
     * @return TriggerDefinition
     */
    public TriggerDefinition setCron(final String cron) {
        this.cron = cron;
        return this;
    }

    /**
     *  Build name
     * @return String
     */
    private String buildName() {
        return isEmpty(name) ? randomUUID().toString() : name;
    }

    /**
     * Building a Trigger
     *
     * @return the Trigger associated with this definition
     */
    public Trigger buildTrigger() {
        if (!isEmpty(cron)) {
            if (!isValidExpression(cron))
                throw new IllegalArgumentException("Provided expression " + cron + " is not a valid cron expression");
            return newTrigger()
                    .withIdentity(buildName(), group)
                    .withSchedule(cronSchedule(cron)
                            .withMisfireHandlingInstructionFireAndProceed()
                            .inTimeZone(TimeZone.getTimeZone(systemDefault())))
                    .usingJobData(CRON, cron)
                    .build();
        } else if (!isEmpty(fireTime)) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(FIRE_TIME, fireTime);
            return newTrigger()
                    .withIdentity(buildName(), group)
                    .withSchedule(simpleSchedule()
                            .withMisfireHandlingInstructionNextWithExistingCount())
                    .startAt(Date.from(fireTime.atZone(systemDefault()).toInstant()))
                    .usingJobData(jobDataMap)
                    .build();
        }
        throw new IllegalStateException("unsupported trigger definition " + this);
    }

    /**
     * Build a definition from Trigger
     *
     * @param trigger the Trigger used to build this definition
     * @return the TriggerDefinition
     */
    public static TriggerDefinition buildDefinition(Trigger trigger) {
        return new TriggerDefinition()
                .setName(trigger.getKey().getName())
                .setGroup(trigger.getKey().getGroup())
                .setFireTime((LocalDateTime) trigger.getJobDataMap().get(FIRE_TIME))
                .setCron(trigger.getJobDataMap().getString(CRON));
    }
}
