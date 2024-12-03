package com.alex.d.springbootatm.messaging;

public enum KafkaTopic {
    ATM_TOPIC("atm-topic"),
    KAFKA_MANAGER_TOPIC("manager-topic"),
    KAFKA_TRANSACTION_TOPIC("transaction-topic");

    private String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
