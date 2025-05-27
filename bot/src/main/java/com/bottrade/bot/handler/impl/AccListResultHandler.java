package com.bottrade.bot.handler.impl;

import com.alibaba.fastjson.JSON;
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

import java.lang.reflect.Type;

@Component
@Slf4j
public class AccListResultHandler implements IBotCmdResultHandler<String> {

    @Autowired
    private BotMsgGenerator botMsgGenerator;

    @Autowired
    private BotDispatcher botDispatcher;

    @Override
    public String cmd() {
        return Constant.BotCmd.accList.getCmd();
    }

    @Override
    public void handleResult(String body) {
        try {
            BotCmdResult<String> cmdResult = convert(body);
            botDispatcher.execute(cmdResult.getBotUsername(),botMsgGenerator.buildStringMsg(cmdResult));
        } catch (Exception e) {
            log.info("[AccListResultHandler] error = "+e);
        }
    }

    @Override
    public TypeToken<BotCmdResult<String>> typeToken() {
        return new TypeToken<BotCmdResult<String>>() {};
    }
}
