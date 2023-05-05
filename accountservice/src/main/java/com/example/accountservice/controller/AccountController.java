package com.example.accountservice.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.accountservice.model.AccountDTO;
import com.example.accountservice.model.MessageDTO;
import com.example.accountservice.model.StatisticDTO;

@RestController
@RequestMapping("/account")
public class AccountController {
	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;
	
	@PostMapping("/new")
	public AccountDTO create(@RequestBody AccountDTO account) {
		StatisticDTO stat = new StatisticDTO("Account " + account.getEmail() + " is created", new Date());
		
		// send notification
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setTo(account.getEmail());
		messageDTO.setToName(account.getName());
		messageDTO.setSubject("Welcome to ABC");
		messageDTO.setContent("ABC is online learning platform.");
		
		kafkaTemplate.send("notification", messageDTO);
		kafkaTemplate.send("statistic", stat);
		
		return account;
	}
}
