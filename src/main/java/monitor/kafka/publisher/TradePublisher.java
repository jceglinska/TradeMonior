package monitor.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import monitor.kafka.serializer.TradeSerializer;
import monitor.model.Trade;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;

@Slf4j
public class TradePublisher {
    private Producer<String, Trade> producer;

    public TradePublisher(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TradeSerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    public void publishMessage(List<Trade> trades, String topic) {
        trades.forEach(t -> {
            ProducerRecord producerRecord = new ProducerRecord(topic, t.getTradeReference(), t);
            producer.send(producerRecord);
            System.out.println("Message was send: " + t.getTradeReference());
        });
        producer.close();
    }
}
