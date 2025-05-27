package com.bottrade.bot.listener;

import com.bottrade.bot.handler.IBotCmdResultHandler;
import com.bottrade.bot.model.tgbot.BotDispatcher;
import com.bottrade.bot.model.tgbot.OrderNotifyBot;
import com.bottrade.bot.model.tgbot.OrderNotifyBotTest;
import com.bottrade.model.Constant;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.OrderResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@Slf4j
@RocketMQMessageListener(topic = Constant.Topics.CMD_RESULT, consumerGroup = Constant.Groups.CMD_RESULT)
public class BotCmdResultListener implements RocketMQListener<Message> {

    @Autowired
    private List<IBotCmdResultHandler> botCmdResultHandlers;

    @Override
    public void onMessage(Message message) {
        Constant.BotCmd botCmd = Constant.BotCmd.of(message.getTags());
        if(botCmd != null){
            Optional<IBotCmdResultHandler> handler = botCmdResultHandlers.stream().filter(iBotCmdResultHandler -> Objects.equals(iBotCmdResultHandler.cmd(), botCmd.getCmd())).findFirst();
            handler.ifPresent(iBotCmdResultHandler -> iBotCmdResultHandler.handleResult(new String(message.getBody())));
        }
    }
}
