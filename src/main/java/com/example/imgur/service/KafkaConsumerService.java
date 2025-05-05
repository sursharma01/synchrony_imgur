package com.example.imgur.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "user-images-topic", groupId = "my-group")
    public void listen(ConsumerRecord<String, String> record) {
        String username = record.key();
        String imageName = record.value();
        log.info("Received message: username = {}, imageName = {}", username, imageName);
    }
}