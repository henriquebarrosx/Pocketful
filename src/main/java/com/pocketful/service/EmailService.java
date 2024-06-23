package com.pocketful.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;
import java.io.IOException;

import freemarker.template.Template;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final Configuration configuration;

    public void send(String to, String subject, Template template, Map<String, Object> model) {
        try {
            log.info("Sending notification: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("support@pocketful.com.br");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(getContent(template, model));

            mailSender.send(message);
        } catch (Exception error) {
            log.error("Something wrong when sending notification to phone number: {}, {}", to, error.getMessage());
        }
    }

    public Template getTemplate(String file) {
        try {
            return configuration.getTemplate(file);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Template %s not found", file));
        }
    }

    private String getContent(Template template, Map<String, Object> model) throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }
}
