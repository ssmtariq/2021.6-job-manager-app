package com.ssmtariq.jobmanager.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.ssmtariq.jobmanager.model.JobDefinition;
import com.ssmtariq.jobmanager.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/job-manager")
@RequiredArgsConstructor
public class JobController {
	private final EmailService emailService;

	/**
	 * Create a job
	 * @param group String
	 * @param definition JobDefinition
	 * @return JobDefinition
	 */
	@PostMapping(path = "/groups/{group}/jobs")
	public ResponseEntity<JobDefinition> createJob(@PathVariable String group, @RequestBody JobDefinition definition) {
		return new ResponseEntity<>(emailService.createJob(group, definition), CREATED);
	}

	/**
	 * Find an existing job
	 * @param group String
	 * @param name String
	 * @return JobDefinition
	 */
	@GetMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<JobDefinition> findJob(@PathVariable String group, @PathVariable String name) {
		return emailService.findJob(group, name)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Update a job
	 * @param group String
	 * @param name String
	 * @param definition JobDefinition
	 */
	@PutMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<Void> updateJob(@PathVariable String group, @PathVariable String name, @RequestBody JobDefinition definition) {
		emailService.updateJob(group, name, definition);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Delete a job
	 * @param group String
	 * @param name String
	 */
	@DeleteMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<Void> deleteJob(@PathVariable String group, @PathVariable String name) {
		emailService.deleteJob(group, name);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Pause a Job
	 * @param group String
	 * @param name String
	 */
	@PatchMapping(path = "/groups/{group}/jobs/{name}/pause")
	public ResponseEntity<Void> pauseJob(@PathVariable String group, @PathVariable String name) {
		emailService.pauseJob(group, name);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Resume a Job
	 * @param group String
	 * @param name String
	 */
	@PatchMapping(path = "/groups/{group}/jobs/{name}/resume")
	public ResponseEntity<Void> resumeJob(@PathVariable String group, @PathVariable String name) {
		emailService.resumeJob(group, name);
		return ResponseEntity.noContent().build();
	}
}
