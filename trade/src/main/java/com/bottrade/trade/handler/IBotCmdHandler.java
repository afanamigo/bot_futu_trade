package com.bottrade.trade.handler;

import com.bottrade.model.bot.BotCmdInfo;

import java.io.Serializable;

public interface IBotCmdHandler {

    String cmd();

    void handle(BotCmdInfo cmdInfo);

}
