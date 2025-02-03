package dev.dini.notification.service.sms;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void sendSms(String toPhoneNumber, String fromPhoneNumber, String messageBody) {
        Message message = Message
                .creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(fromPhoneNumber),
                        messageBody
                )
                .create();

        System.out.println("Message sent with SID: " + message.getSid());
    }



}
