package com.bottrade.bot.handler.impl;

import com.bottrade.bot.handler.IBotCmdResultHandler;
import com.bottrade.bot.model.BotMsgGenerator;
import com.bottrade.bot.model.tgbot.BotDispatcher;
import com.bottrade.model.Constant;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.TradeInfo;
import com.google.common.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InfoResultHandler implements IBotCmdResultHandler<TradeInfo> {

    @Autowired
    private BotMsgGenerator botMsgGenerator;

    @Autowired
    private BotDispatcher botDispatcher;

    @Override
    public String cmd() {
        return Constant.BotCmd.info.getCmd();
    }

    @Override
    public void handleResult(String body) {
        try {
            log.info("[InfoResultHandler] body = "+body);
            BotCmdResult<TradeInfo> cmdResult = convert(body);
            botDispatcher.execute(cmdResult.getBotUsername(),botMsgGenerator.buildInfoMsg(cmdResult));
        } catch (Exception e) {
            log.error("[InfoResultHandler] error = ", e);
        }
    }

    @Override
    public TypeToken<BotCmdResult<TradeInfo>> typeToken() {
        return new TypeToken<BotCmdResult<TradeInfo>>() {};
    }
}
