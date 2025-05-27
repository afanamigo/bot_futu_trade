package com.bottrade.bot.handler;

import com.alibaba.fastjson.JSON;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.TradeInfo;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

public interface IBotCmdResultHandler<T> {

    String cmd();

    void handleResult(String body);

    TypeToken<BotCmdResult<T>> typeToken();

    default BotCmdResult<T> convert(String body){
        Type type = typeToken().getType();
        return JSON.parseObject(body, type);
    }
}
