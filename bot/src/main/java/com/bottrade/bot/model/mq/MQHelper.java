package com.bottrade.bot.model.mq;

import com.bottrade.model.OrderDTO;
import com.bottrade.model.bot.BotCmdInfo;
import com.bottrade.model.Constant;
import com.bottrade.model.OrderUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class MQHelper {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void publishSimpleOrder(OrderUpdateDTO dto){
        SendResult sendResult = rocketMQTemplate.syncSend(Constant.Topics.SIMPLE_ORDER,dto);
        log.info("[publishSimpleOrder]send result: " + sendResult);
    }

    public void publishOrder(OrderUpdateDTO dto){
        SendResult sendResult = rocketMQTemplate.syncSend(Constant.Topics.ORDER,dto);
        log.info("[publishOrder]send result: " + sendResult);
    }

    public void publishCmd(BotCmdInfo cmdInfo){
        SendResult sendResult = rocketMQTemplate.syncSend(Constant.Topics.CMD,cmdInfo);
        log.info("[publishCmd]send result: " + sendResult);
    }

    public void publishUpdateOrder(Map<String, List<OrderDTO>> orderMap){
        SendResult sendResult = rocketMQTemplate.syncSend(Constant.Topics.ORDER_UPDATE,orderMap);
        log.info("[publishUpdateOrder]send result: " + sendResult);
    }

}
