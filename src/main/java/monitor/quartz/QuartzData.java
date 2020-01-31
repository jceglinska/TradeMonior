package monitor.quartz;

import monitor.trade.TradeMonitor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static monitor.Consts.FILES_DIRECTORY;
import static monitor.Consts.KAFKA_BOOTSTRAP_SERVERS;
import static monitor.Consts.KAFKA_TOPIC;
import static monitor.Consts.PROPERTIES;
import static monitor.Consts.QUARTZ_GROUP;
import static monitor.Consts.QUARTZ_INTERVAL_SEC;
import static monitor.Consts.QUARTZ_JOB;
import static monitor.Consts.QUARTZ_TRIGGER;

public class QuartzData {
    private Properties properties = null;
    {
        try {
            properties = getProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JobDetail getJobDetail() {
        return JobBuilder.newJob(TradeMonitorJob.class)
                .withIdentity(QUARTZ_JOB, QUARTZ_GROUP)
                .usingJobData(FILES_DIRECTORY, properties.getProperty(FILES_DIRECTORY))
                .usingJobData(KAFKA_TOPIC, properties.getProperty(KAFKA_TOPIC))
                .usingJobData(KAFKA_BOOTSTRAP_SERVERS, properties.getProperty(KAFKA_BOOTSTRAP_SERVERS))
                .build();
    }

    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity(QUARTZ_TRIGGER, QUARTZ_GROUP)
                .startNow()
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(QUARTZ_INTERVAL_SEC)
                        .repeatForever())
                .build();
    }

    private Properties getProperties() throws IOException {
        try (InputStream input = TradeMonitor.class.getClassLoader().getResourceAsStream(PROPERTIES)) {
            java.util.Properties prop = new java.util.Properties();
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            throw new IOException("no properties found");
        }
    }
}
