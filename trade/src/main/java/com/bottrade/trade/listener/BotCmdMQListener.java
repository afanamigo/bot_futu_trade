package com.bottrade.trade.listener;

import com.bottrade.model.bot.BotCmdInfo;
import com.bottrade.model.Constant;
import com.bottrade.trade.handler.IBotCmdHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@Slf4j
@RocketMQMessageListener(topic = Constant.Topics.CMD, consumerGroup = Constant.Groups.CMD)
public class BotCmdMQListener implements RocketMQListener<BotCmdInfo> {

    @Autowired
    private List<IBotCmdHandler> botCmdHandlers;

    @Override
    public void onMessage(BotCmdInfo cmdInfo) {
        Constant.BotCmd botCmd = Constant.BotCmd.of(cmdInfo.getCmd());
        if(botCmd != null){
            Optional<IBotCmdHandler> handler = botCmdHandlers.stream().filter(iBotCmdHandler -> Objects.equals(iBotCmdHandler.cmd(), botCmd.getCmd())).findFirst();
            handler.ifPresent(iBotCmdHandler -> iBotCmdHandler.handle(cmdInfo));
        }
    }
}
