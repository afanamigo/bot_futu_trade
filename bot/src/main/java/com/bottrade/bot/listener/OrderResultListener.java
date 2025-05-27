package com.bottrade.bot.listener;

import com.bottrade.bot.model.tgbot.BotDispatcher;
import com.bottrade.bot.model.tgbot.OrderNotifyBot;
import com.bottrade.bot.model.tgbot.OrderNotifyBotTest;
import com.bottrade.model.Constant;
import com.bottrade.model.bot.OrderResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Objects;

@Component
@Slf4j
@RocketMQMessageListener(topic = Constant.Topics.ORDER_RESULT, consumerGroup = Constant.Groups.RESULT)
public class OrderResultListener implements RocketMQListener<OrderResult> {

    @Autowired
    private BotDispatcher botDispatcher;

    @Override
    public void onMessage(OrderResult result) {
        if(result != null){

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(result.getSourceChatId());
            sendMessage.setText(toBotText(result));
            try {
                botDispatcher.execute(result.getBotUsername(),sendMessage);
            } catch (Exception e) {
                log.info("[OrderResultListener] error = " + e);
            }
        }
    }

    private String toBotText(OrderResult result){
        StringBuilder sb = new StringBuilder();
        if(result.isSuc()){
            sb.append("【下单成功】").append("\n");
        }else {
            sb.append("【下单失败】").append(result.getErrorMsg()).append("\n");
        }
        if(!StringUtils.isEmpty(result.getOrderId())){
            sb.append("【订单ID】").append(result.getOrderId());
        }
        return sb.toString();
    }
}
