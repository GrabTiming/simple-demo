package com.Lnn.consumer;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * topic、consumerGroup是必填，确定消费的消息主题以及消费者
 * selectorType 过滤类型，有SelectorType.TAG和SelectorType.SQL92两种
 * selectorExpression 选择器表达式，选择哪些消息
 * messageModel 消息模型，广播或集群模式 MessageModel.BROADCASTING 或 MessageModel.CLUSTERING
 * consumeMode 消费模式，可选同步或异步 ,ConsumeMode.ORDERLY 或 ConsumeMode.CONCURRENTLY
 */
@Component
@RocketMQMessageListener(consumerGroup = "consumer-group-01",
                         topic = "myTopic")
public class MyConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("收到消息: "+s);
    }
}
