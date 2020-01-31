package monitor.quartz;

import lombok.extern.slf4j.Slf4j;
import monitor.kafka.publisher.TradePublisher;
import monitor.model.Trade;
import monitor.reader.JsonFileReader;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static monitor.Consts.FILES_DIRECTORY;
import static monitor.Consts.KAFKA_BOOTSTRAP_SERVERS;
import static monitor.Consts.KAFKA_TOPIC;

@Slf4j
public class TradeMonitorJob implements Job {
    private static Set<String> tradesSet = new HashSet<>();
    private TradePublisher tpub = null;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<Trade> trades = new ArrayList<>();
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        JsonFileReader reader = new JsonFileReader();

        String directory = dataMap.getString(FILES_DIRECTORY);
        String topic = dataMap.getString(KAFKA_TOPIC);
        String servers = dataMap.getString(KAFKA_BOOTSTRAP_SERVERS);

        try {
            List<Trade> readData = completeTradeData(flattenListOfListsStream(reader.getTrades(directory)));
            trades = getFiltredTrades(trades, readData);

        } catch (IOException e) {
            log.error("Cannot retrieve Trade data");
        }

        System.out.println(directory + " | " + topic + " | " + servers);
        trades.forEach(System.out::println);

        tpub = new TradePublisher(servers);
        tpub.publishMessage(trades, topic);

    }

    private List<Trade> getFiltredTrades(List<Trade> trades, List<Trade> readData) {
        trades.addAll(readData);

        List<String> filteredListOfReferences = trades.stream()
                .map(Trade::getTradeReference)
                .filter(t -> !tradesSet.contains(t))
                .collect(Collectors.toList());

        trades = trades.stream()
                .filter(p -> filteredListOfReferences.contains(p.getTradeReference()))
                .collect(Collectors.toList());

        tradesSet.addAll(readData.stream()
                .map(Trade::getTradeReference)
                .collect(Collectors.toList()));

        return trades;
    }

    private static <T> List<T> flattenListOfListsStream(List<List<T>> list) {
        return list.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Trade> completeTradeData(List<Trade> trades) {
        return trades.stream()
                .map(this::completeTrade)
                .collect(Collectors.toList());
    }

    private Trade completeTrade(Trade trade) {
        trade.setReceivedTimestamp(new Timestamp(System.currentTimeMillis()));
        trade.setAmount(trade.getPrice().multiply(trade.getQuantity()).setScale(2, RoundingMode.HALF_UP));
        return trade;
    }
}
