package com.bottrade.model.bot;

import lombok.Data;

import java.io.Serializable;

@Data
public class BotCmdResult<T> extends BaseBotResult{

    private String cmd;

    private T result;

    public BotCmdResult() {
    }

    public BotCmdResult(BotInfo botInfo) {
        this.sourceChatId = botInfo.sourceChatId;
        this.botUsername = botInfo.getBotUsername();
    }
}
