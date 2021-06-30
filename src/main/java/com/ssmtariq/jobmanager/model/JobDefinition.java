package com.ssmtariq.jobmanager.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ssmtariq.jobmanager.constant.ApplicationConstants.BCC;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.CC;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.MESSAGE_BODY;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.SUBJECT;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.TO;
import static org.quartz.JobBuilder.*;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssmtariq.jobmanager.job.EmailJob;

import lombok.Data;

@Data
public class JobDefinition {
    private String name;
    private String group;
    private String subject;
    private String messageBody;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private Map<String, Object> data = new LinkedHashMap<>();
    @JsonProperty("triggers")
    private List<TriggerDefinition> triggerDefinition = new ArrayList<>();

    /**
     * Set JobDefinition name
     * @param name String
     * @return JobDefinition
     */
    public JobDefinition setName(final String name) {
        this.name = name;
        return this;
    }

    /**
     * Set JobDefinition group
     * @param group String
     * @return JobDefinition
     */
    public JobDefinition setGroup(final String group) {
        this.group = group;
        return this;
    }

    /**
     * Set JobDefinition subject
     * @param subject String
     * @return JobDefinition
     */
    public JobDefinition setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Set JobDefinition messageBody
     * @param messageBody String
     * @return JobDefinition
     */
    public JobDefinition setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    /**
     * Set JobDefinition to
     * @param to List<String>
     * @return JobDefinition
     */
    public JobDefinition setTo(List<String> to) {
        this.to = to;
        return this;
    }

    /**
     * Set JobDefinition cc
     * @param cc List<String>
     * @return JobDefinition
     */
    public JobDefinition setCc(List<String> cc) {
        this.cc = cc;
        return this;
    }

    /**
     * Set JobDefinition bcc
     * @param bcc List<String>
     * @return JobDefinition
     */
    public JobDefinition setBcc(List<String> bcc) {
        this.bcc = bcc;
        return this;
    }

    /**
     * Set JobDefinition triggerDefinition
     * @param triggerDefinition List<TriggerDefinition>
     * @return JobDefinition
     */
    public JobDefinition setTriggerDefinition(final List<TriggerDefinition> triggerDefinition) {
        this.triggerDefinition = triggerDefinition;
        return this;
    }

    /**
     * Building Triggers of Job
     *
     * @return Triggers for this JobDetail
     */
    @JsonIgnore
    public Set<Trigger> buildTriggers() {
        Set<Trigger> triggers = new LinkedHashSet<>();
        for (TriggerDefinition triggerDescriptor : triggerDefinition) {
            triggers.add(triggerDescriptor.buildTrigger());
        }

        return triggers;
    }

    /**
     * Build a JobDetail
     *
     * @return the JobDetail built from this definition
     */
    public JobDetail buildJobDetail() {
        JobDataMap jobDataMap = new JobDataMap(getData());
        jobDataMap.put(SUBJECT, subject);
        jobDataMap.put(MESSAGE_BODY, messageBody);
        jobDataMap.put(TO, to);
        jobDataMap.put(CC, cc);
        jobDataMap.put(BCC, bcc);
        return newJob(EmailJob.class)
                .withIdentity(getName(), getGroup())
                .usingJobData(jobDataMap)
                .build();
    }

    /**
     * Build a definition from JobDetail and Triggers
     *
     * @param jobDetail     the JobDetail instance
     * @param triggersOfJob the Triggers to associate with the Job
     * @return the JobDetails
     */
    @SuppressWarnings("unchecked")
    public static JobDefinition buildDefinition(JobDetail jobDetail, List<? extends Trigger> triggersOfJob) {
        List<TriggerDefinition> triggerDetails = new ArrayList<>();

        for (Trigger trigger : triggersOfJob) {
            triggerDetails.add(TriggerDefinition.buildDefinition(trigger));
        }

        return new JobDefinition()
                .setName(jobDetail.getKey().getName())
                .setGroup(jobDetail.getKey().getGroup())
                .setSubject(jobDetail.getJobDataMap().getString(SUBJECT))
                .setMessageBody(jobDetail.getJobDataMap().getString(MESSAGE_BODY))
                .setTo((List<String>) jobDetail.getJobDataMap().get(TO))
                .setCc((List<String>) jobDetail.getJobDataMap().get(CC))
                .setBcc((List<String>) jobDetail.getJobDataMap().get(BCC))
                .setTriggerDefinition(triggerDetails);
    }
}
