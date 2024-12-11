package com.alex.d.springbootatm.messaging;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private Gson gson;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Object message) {
        log.info("--------------------------------");
        log.info("Sending message: {}", message);
        log.info("--------------------------------");
        kafkaTemplate.send(topic, message);
    }

    public void setKafkaProducerServiceMessage(Object data, String topic) {
        String message = gson.toJson(data);
        sendMessage(topic, message);
    }
}