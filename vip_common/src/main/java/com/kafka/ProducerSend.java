package com.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerSend {
    private static KafkaProducer<String, String> createProducer() {
        return new KafkaProducer<String, String>(kafkaConfig.getProperties("kafka"));
    }
    public static void sendMessage(String topic,String message){
        try {
            Properties props = kafkaConfig.getProperties("kafka");
            KafkaProducer<String, String> producer = createProducer();
            producer.send(new ProducerRecord<String, String>(topic+props.getProperty("topic.env"), message));
            producer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @date: 2019/5/16
     * @author: jihaiyang
     * @mark: 测试kafka发送消费是否正常
     */
    public static void main(String args[]) {

        //1.属性配置：端口、缓冲内存、最大连接数、key序列化、value序列化等等
		 /*
		 Properties props=new Properties();
		 props.put("bootstrap.servers", "localhost:9092");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");*/


        //2.创建生产者对象，并建立连接,通过我们自己创建的 kafkaConfig 工具类获得配置文件（根据传入的值，获得相对应的properties文件）
        for (int i =0 ; i<1000;i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userid", "1");
            jsonObject.put("etrustid", "2");
            jsonObject.put("etrustsum", "3");
            jsonObject.put("entrustmarket", "4");
            jsonObject.put("fundstype", "5");
            ProducerSend producerSend = new ProducerSend();
            producerSend.sendMessage("abc", jsonObject.toString());
        }
    }

}
