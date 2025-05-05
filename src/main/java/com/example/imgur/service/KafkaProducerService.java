package com.example.imgur.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class KafkaProducerService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private KafkaProducer<String, String> kafkaProducer;

    public void sendMessage(String username, String imageName) {
        kafkaProducer = getKafkaConfig();
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("user-images-topic", username, imageName);
        kafkaProducer.send(producerRecord);
    }

    private KafkaProducer<String, String> getKafkaConfig() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return  new KafkaProducer<>(properties);
    }
}

