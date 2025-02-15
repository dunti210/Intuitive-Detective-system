import com.migcomponents.migbase64.Base64;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SimpleConsumer {

    private final ConsumerConnector consumer;
    private final String topic;


    public SimpleConsumer(String zookeeper, String groupId, String topic) {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("group.id", groupId);
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");                                                                                                                                                                                              
        props.put("zookeeper.session.timeout.ms", "500");
        props.put("zookeeper.sync.time.ms", "250");
        props.put("auto.commit.interval.ms", "1000");

//        KafkaConsumer<Integer, String> kafkaConsumer = new KafkaConsumer<Integer, String>(props);
//        kafkaConsumer.subscribe(Arrays.asList(topic));
        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        this.topic = topic;

        System.out.println("Topic is: "+ topic);
    }

    public void DecodeVideo(String encodedString, int k){
        byte[] decodedBytes = Base64.decodeFast(encodedString.getBytes());

        try {
            FileOutputStream out = new FileOutputStream("decode" + k + ".jpg");
            out.write(decodedBytes);
            out.close();
            System.out.println("Created File");
        } catch (Exception e) {
            // TODO: handle exception
//            Log.e("Error", e.toString());
            System.out.println("I am here6: ");

        }
    }

    public void testConsumer() {
        Map<String, Integer> topicCount = new HashMap<String, Integer>();
        topicCount.put(topic, 1);
        System.out.println("I am here1: ");

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
        List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
        System.out.println("I am here2: ");
        StringBuilder sb = new StringBuilder();
        for ( KafkaStream stream : streams) {
            System.out.println("I am here3: ");
            //System.out.println("Stream size: " + stream.);
            int j=0;
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            System.out.println("I am here4: ");
            int i=0;
            System.out.println("Iterator value" + it.isEmpty());

            while (it.hasNext()) {
                System.out.println("Inside Consumer Iterator");
                System.out.println("Value: " + it.hasNext() );

                byte[] message = it.next().message();
                try {
                    sb.setLength(0);

                    String value = new String(message, "UTF-8");
                    System.out.println("I am here5: ");

                    sb.append(value);
                    System.out.println("Creating Video");
                    DecodeVideo(sb.toString(), j);
                    j++;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
               // i++;

               // if(i > 1){


               // j++;
}
           // }
//            System.out.println("Creating Video");
//                    DecodeVideo(sb.toString());
        }
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    public static void main(String[] args) {
        String topic = "priya"; //args[0]; //Topic Name
        SimpleConsumer simpleConsumer = new SimpleConsumer("localhost:2181", "testgroup", topic);

       System.out.println("Priya");
        simpleConsumer.testConsumer();
    }

}