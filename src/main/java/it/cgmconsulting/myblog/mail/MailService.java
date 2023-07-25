package it.cgmconsulting.myblog.mail;

import com.mailgun.model.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.MessageResponse;

@Service
@Slf4j
public class MailService {

    @Value("${app.mail.apiKey}")
    private String mailgunApiyKey;
    @Value("${app.mail.domain}")
    private String mailgunDomain;

    public MessageResponse sendMail(Mail mail) {
        MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(mailgunApiyKey).createApi(MailgunMessagesApi.class);

        Message message = Message.builder()
                .from(mail.getMailFrom())
                .to(mail.getMailTo())
                .subject(mail.getMailSubject())
                .text(mail.getMailContent())
                .build();

        return mailgunMessagesApi.sendMessage(mailgunDomain, message);
    }
}
