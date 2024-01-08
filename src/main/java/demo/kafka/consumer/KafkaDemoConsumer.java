package demo.kafka.consumer;

import demo.kafka.event.DemoInboundEvent;
import demo.kafka.service.DemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaDemoConsumer {

    final DemoService demoService;

    @KafkaListener(
            topics = "#{'${kafka.inboundTopic}'}",
            groupId = "demo-consumer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload DemoInboundEvent event) {
        log.info("Received message - event: " + event);
        try {
            processMessage(rec); // Call to process the message for tracing
            demoService.process(event.getNumberOfEvents());
        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
        }
    }

    @Trace(dispatcher = true)
    private static void processMessage(ConsumerRecord<String, String> rec) {
        Headers dtHeaders = ConcurrentHashMapHeaders.build(HeaderType.MESSAGE);
        for (Header header : rec.headers()) {
            String headerValue = new String(header.value(), StandardCharsets.UTF_8);
            if (header.key().equals("newrelic")) {
                dtHeaders.addHeader("newrelic", headerValue);
            }
            if (header.key().equals("traceparent")) {
                dtHeaders.addHeader("traceparent", headerValue);
            }
            if (header.key().equals("tracestate")) {
                dtHeaders.addHeader("tracestate", headerValue);
            }
        }
        NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Kafka, dtHeaders);
    }
}
