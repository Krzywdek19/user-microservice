package com.krzywdek19.user_service.service.impl;

import com.krzywdek19.user_service.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private final Logger logger = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("EMAIL → to: {} | subject: {} | body: {}", to, subject, body);
    }
}
