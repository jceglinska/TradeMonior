package monitor.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class TradeSerializer implements Serializer {

    @Override
    public byte[] serialize(String s, Object o) {
        byte[] byteValue = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byteValue = objectMapper.writeValueAsString(o).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteValue;
    }
}

