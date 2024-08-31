package com.Lnn;

import com.Lnn.producer.MyProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MQTest {

    @Autowired
    private MyProducer producer;

    @Test
    public void test01(){
        producer.sendMessage("myTopic","hello world 2024.8.27");
    }

}
