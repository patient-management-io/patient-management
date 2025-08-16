package com.pm.billingservice.kafka;

import billing.events.BillingAccountEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "billing", groupId = "billing-service")
    public void consumeEvent(byte[] event){
        try {
            BillingAccountEvent billingEvent = BillingAccountEvent.parseFrom(event);
            // ... perform any business related to billing here
            log.info("Received Billing Account Create Request Event: [PatientId: {}, EventType: {}]",
                    billingEvent.getPatientId(),
                    billingEvent.getEventType()
            );
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event: {}", e.getMessage());
        }
    }
}
