package com.ssmtariq.jobmanager.job;

import static com.ssmtariq.jobmanager.constant.ApplicationConstants.BCC;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.CC;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.MAIL_FROM;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.MESSAGE_BODY;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.SUBJECT;
import static com.ssmtariq.jobmanager.constant.ApplicationConstants.TO;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailJob implements Job {
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * Execute the job
	 * @param context JobExecutionContext
	 */
	@Override
	public void execute(JobExecutionContext context) {
		log.info("Job triggered to send emails");
		JobDataMap map = context.getMergedJobDataMap();
		sendEmail(map);
		log.info("Job completed");
	}

	/**
	 * Send Emails
	 * @param map JobDataMap
	 */
	@SuppressWarnings("unchecked")
	private void sendEmail(JobDataMap map) {
		String subject 	   = map.getString(SUBJECT);
		String messageBody = map.getString(MESSAGE_BODY);
		List<String> to    = (List<String>) map.get(TO);
		List<String> cc	   = (List<String>) map.get(CC);
		List<String> bcc   = (List<String>) map.get(BCC);
		
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false);
			for(String receipient : to) {
				helper.setFrom(MAIL_FROM);
				helper.setTo(receipient);
				helper.setSubject(subject);
				helper.setText(messageBody);
				if(!isEmpty(cc))
					helper.setCc(cc.stream().toArray(String[]::new));
				if(!isEmpty(bcc))
					helper.setBcc(bcc.stream().toArray(String[]::new));
				mailSender.send(message);
			}
		} catch (MessagingException e) {
			log.error("An error occurred: {}", e.getLocalizedMessage());
		}
	}
}
