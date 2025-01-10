package dev.dini.notification_service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Email {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
}
