package dev.dini.notification.service.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public String sendSms(@RequestParam String to, @RequestParam String from, @RequestParam String message) {
        smsService.sendSms(to, from, message);
        return "SMS Sent Successfully!";
    }
}
