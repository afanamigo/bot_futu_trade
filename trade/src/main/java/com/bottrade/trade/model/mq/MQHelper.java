package com.bottrade.trade.model.mq;

import com.alibaba.fastjson.JSON;
import com.bottrade.model.Constant;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.OrderResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
@Slf4j
public class MQHelper {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void notifyResult(OrderResult result){
        SendResult sendResult = rocketMQTemplate.syncSend(Constant.Topics.ORDER_RESULT,result);
        log.info("[notifyResult] send result: " + sendResult);
    }

    public <T extends Serializable> void notifyCmdResult(BotCmdResult<T> result){
        try {
            Message message = new Message();
            message.setTopic(Constant.Topics.CMD_RESULT);
            message.setTags(result.getCmd());
            message.setBody(JSON.toJSONBytes(result));
            SendResult sendResult = rocketMQTemplate.getProducer().send(message);
            log.info("[notifyCmdResult] send result: " + sendResult);
        } catch (Exception e) {
            log.info("[notifyCmdResult] Exception: " + e);
        }
    }

}
