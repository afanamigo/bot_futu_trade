package com.bottrade.model.bot;

import lombok.Data;

import java.io.Serializable;

@Data
public class BotInfo implements Serializable {

    Long sourceChatId;

    String botUsername;

}
