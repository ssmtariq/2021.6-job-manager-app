package com.ssmtariq.jobmanager.service;

import com.ssmtariq.jobmanager.model.JobDefinition;

import java.util.Optional;

public interface JobService {

    /**
     * Create a job
     * @param group String
     * @param definition JobDefinition
     * @return JobDefinition
     */
    JobDefinition createJob(String group, JobDefinition definition);

    /**
     * Find an existing job
     * @param group String
     * @param name String
     * @return JobDefinition
     */
    Optional<JobDefinition> findJob(String group, String name);

    /**
     * Update a job
     * @param group String
     * @param name String
     * @param definition JobDefinition
     */
    void updateJob(String group, String name, JobDefinition definition);

    /**
     * Delete a job
     * @param group String
     * @param name String
     */
    void deleteJob(String group, String name);

    /**
     * Pause a Job
     * @param group String
     * @param name String
     */
    void pauseJob(String group, String name);

    /**
     * Resume a Job
     * @param group String
     * @param name String
     */
    void resumeJob(String group, String name);
}
