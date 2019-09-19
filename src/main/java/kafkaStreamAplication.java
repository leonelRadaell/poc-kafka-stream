
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.log4j.BasicConfigurator;

public class kafkaStreamAplication {

    public static Properties getKafkaConfiguration() {
        System.out.println("INICIO");
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "plaintext://172.16.18.141:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

        KStreamBuilder builder = new KStreamBuilder();

        KStream wordCountsInput = builder.stream("input-topic");
        KTable counts = wordCountsInput.mapValues(v -> v.toString().toUpperCase())
                .flatMapValues(value -> Arrays.asList(value.toString().split(" "))).selectKey((ignoredKey, word) -> word)
                .groupByKey().count();

        counts.to(Serdes.String(), Serdes.Long(), "output-topic");

        KafkaStreams streams = new KafkaStreams(builder, getKafkaConfiguration());
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}