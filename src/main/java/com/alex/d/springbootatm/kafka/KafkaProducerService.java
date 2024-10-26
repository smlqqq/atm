package com.alex.d.springbootatm.kafka;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private Gson gson;


    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }

    public void setKafkaProducerService(Object data, String topic) {
        String message = gson.toJson(data);
        sendMessage(topic, message);
    }
}