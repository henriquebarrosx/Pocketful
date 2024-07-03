package com.pocketful.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final Configuration configuration;

    public <T> void send(String to, String subject, Template textTemplate, Template htmlTemplate, T model) {
        try {
            log.info("Sending notification: {}", to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            message.setFrom("support@pocketful.com.br");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(getTextBody(textTemplate, model), getHtmlBody(htmlTemplate, model));

            mailSender.send(mimeMessage);
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

    private <T> String getHtmlBody(Template template, T model) throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private <T> String getTextBody(Template template, T model) throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }
}
