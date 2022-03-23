package ru.job4j.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {

    private final KafkaTemplate<Integer, String> template;
    private final PassportService passportService;

    @Value("spring.kafka.default-topic")
    private String topic;

    public SchedulerService(KafkaTemplate<Integer, String> template,
                            PassportService passportService) {
        this.template = template;
        this.passportService = passportService;
    }

    @Scheduled(fixedDelay = 5000)
    public void sendingNotification() {
        System.out.println("Searching for expired passports");
        passportService.findUnavailable()
                .stream()
                .map(passport -> passport.getSerial() + " " + passport.getNumber())
                .forEach(passData -> template.send(topic, passData));
    }
}