package com.bottrade.trade.listener;

import com.bottrade.model.Constant;
import com.bottrade.model.OrderDTO;
import com.bottrade.model.OrderUpdateDTO;
import com.bottrade.model.bot.OrderResult;
import com.bottrade.trade.model.moomoo.MoomooClient;
import com.bottrade.trade.model.mq.MQHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RocketMQMessageListener(topic = Constant.Topics.ORDER_UPDATE, consumerGroup = Constant.Groups.ORDER_UPDATE,messageModel = MessageModel.BROADCASTING)
public class OrderUpdateMQListener implements RocketMQListener<Map<String, List<OrderDTO>>> {

    @Autowired
    private MoomooClient moomooClient;

    @Autowired
    private MQHelper mqHelper;

    @Override
    public void onMessage(Map<String, List<OrderDTO>> dto) {



    }
}
