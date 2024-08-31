package com.Lnn.producer;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 生产者发送消息只需要注入 RocketMQTemplate 然后发送消息
 *
 * 一般来说一个业务有自己的生产者，消费者以及自定义的消息类，这里简单使用String
 */
@Component
public class MyProducer {


    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    //--------------------------------------普通消息--------------------------------------//

    /**
     * 默认异步消息
     */
    public void sendMessage(String topic,String message){
        rocketMQTemplate.convertAndSend(topic,message);
    }

    /**
     * 同步消息
     */
    public void sendMessageSync(String topic, String message){
        rocketMQTemplate.syncSend(topic, message);//同步
    }

    /**
     * 单向发送消息，不能获取发送后的结果
     */
    public void sendMessageOneway(String topic, String message){
        rocketMQTemplate.sendOneWay(topic, message);//单向
    }

    //--------------------------------------顺序消息--------------------------------------//

    /**
     * 普通顺序消息：在同一个topic下，通过sharding key区分的不同队列，每个队列内的消息保证消费的顺序，不同队列间的消费顺序不作要求
     */
    public void sendMessageOrderly(String topic, String key,String message){
        rocketMQTemplate.syncSendOrderly(topic, message, key);
    }
}
