package org.me.main.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailJobService implements Job {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name:spring-app}")
    private String fromName;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String raw = dataMap.getString("receiverEmail");
        String[] toEmails = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        String subject = dataMap.getString("subject");
        String body = dataMap.getString("body");

        try {
            boolean success = sendEmail(toEmails, subject, body);

            if (!success) {
                throw new JobExecutionException("Failed to send email via SendGrid");
            }

            log.info("*** Gửi mail thành công tới: {}", String.join(", ", toEmails));

        } catch (Exception e) {
            log.error("Error sending email via SendGrid", e);
            throw new JobExecutionException("Error sending email", e);
        }
    }

    private boolean sendEmail(String[] toEmails, String subject, String body) {
        boolean allSuccess = true;
        int successCount = 0;
        int totalEmails = toEmails.length;

        SendGrid sg = new SendGrid(sendGridApiKey);
        Email from = new Email(fromEmail, fromName);
        Content content = new Content("text/html", body);

        for (String toEmail : toEmails) {
            try {
                // Tạo mail mới cho mỗi recipient
                Email to = new Email(toEmail);
                Mail mail = new Mail(from, subject, to, content);

                Request request = new Request();
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());

                Response response = sg.api(request);

                boolean isSuccess = response.getStatusCode() >= 200 && response.getStatusCode() < 300;

                if (isSuccess) {
                    successCount++;
                    log.info("Email sent successfully to: {} (Status: {})", toEmail, response.getStatusCode());
                } else {
                    allSuccess = false;
                    log.error("Failed to send email to: {}. Status: {}, Body: {}",
                            toEmail, response.getStatusCode(), response.getBody());
                }

            } catch (IOException e) {
                allSuccess = false;
                log.error("IOException when sending email to: {}", toEmail, e);
            } catch (Exception e) {
                allSuccess = false;
                log.error("Unexpected error when sending email to: {}", toEmail, e);
            }
        }

        log.info("Email sending completed: {}/{} emails sent successfully", successCount, totalEmails);

        // Trả về true nếu gửi được ít nhất 1 email thành công
        // Hoặc có thể thay đổi logic tùy yêu cầu
        return successCount > 0;
    }
}
