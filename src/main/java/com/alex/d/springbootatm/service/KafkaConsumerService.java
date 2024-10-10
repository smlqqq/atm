package com.alex.d.springbootatm.service;

import com.alex.d.springbootatm.dto.BankCardDTO;
import com.alex.d.springbootatm.model.BankCardModel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

    @Autowired
    private ObjectMapper objectMapper;


    @KafkaListener(topics = {"atm-topic", "manager-topic", "transaction-topic"}, groupId = "consumer")
    public void listen(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        //FIXME ИСПРАВИТЬ КОСТЫЛЬ
        if (message.startsWith("\"") && message.endsWith("\"")) {
            message = message.substring(1, message.length() - 1);
            message = message.replaceAll("\\\\", "");
        }

        try {
            switch (topic) {
                case "manager-topic":
                    BankCardDTO bankCard = objectMapper.readValue(message, BankCardDTO.class);
                    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                    log.info("Kafka message from managerController: {}", objectMapper.writeValueAsString(bankCard));
                    break;
                case "transaction-topic":
                    BankCardDTO sender = objectMapper.readValue(message, BankCardDTO.class);
                    BankCardDTO recipient = objectMapper.readValue(message, BankCardDTO.class);
                    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                    log.info("Kafka message from transactionController: {} {}", objectMapper.writeValueAsString(sender), objectMapper.writeValueAsString(recipient));
                    break;
            }
        } catch (Exception e) {
            log.error("Error parsing message", e);
        }
    }

}
