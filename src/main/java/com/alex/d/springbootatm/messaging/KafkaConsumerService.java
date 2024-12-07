package com.alex.d.springbootatm.messaging;

import com.alex.d.springbootatm.model.dto.CardDto;
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

        if (message.startsWith("\"") && message.endsWith("\"")) {
            message = message.substring(1, message.length() - 1);
            message = message.replaceAll("\\\\", "");
        }

        try {
            switch (topic) {
                case "manager-topic":
                    CardDto managerCardDto = objectMapper.readValue(message, CardDto.class);
                    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                    log.info("Kafka message from managerController: {}", objectMapper.writeValueAsString(managerCardDto));
                    break;
                case "transaction-topic":
                    CardDto transactionSenderCardDto = objectMapper.readValue(message, CardDto.class);
                    CardDto transactionrecipientCardDto = objectMapper.readValue(message, CardDto.class);
                    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                    log.info("Kafka message from transactionController: {} {}", objectMapper.writeValueAsString(transactionSenderCardDto), objectMapper.writeValueAsString(transactionrecipientCardDto));
                case "atm-topic":
                    CardDto atmCardDto  = objectMapper.readValue(message, CardDto.class);
                    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                    log.info("Kafka message from atmController: {}", objectMapper.writeValueAsString(atmCardDto));
                    break;
            }
        } catch (Exception e) {
            log.error("Error parsing message", e);
        }
    }

}
