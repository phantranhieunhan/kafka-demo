package com.example.notificationservice.service;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.notificationservice.model.MessageDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


public interface MessageService{
	void sendEmail(MessageDTO messageDTO);
}

@Service
class EmailServiceImpl implements MessageService{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Value("${spring.mail.username}")
	private String from;
	
	@Override
	@Async
	public void sendEmail(MessageDTO messageDTO) {
		try {
			logger.info("Start... Sending email");
			
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
			
			//load template email with content
			Context context = new Context();
			context.setVariable("name", messageDTO.getToName());
			context.setVariable("content", messageDTO.getContent());
			String html = templateEngine.process("welcome-email", context);
			
			// send email
			helper.setTo(messageDTO.getTo());
			helper.setText(html);
			helper.setSubject(messageDTO.getSubject());
			helper.setFrom(from);
			
			javaMailSender.send(message);
			logger.info("END . . .Email sent success");
		}catch(MessagingException e) {
			logger.error("Email sent with error: ", e.getMessage());
		}
	}
	
}
