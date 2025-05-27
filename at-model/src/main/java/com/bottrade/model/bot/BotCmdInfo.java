package com.bottrade.model.bot;

import lombok.Data;

@Data
public class BotCmdInfo extends BotInfo {

    private String cmd;

    private String extra;

}
