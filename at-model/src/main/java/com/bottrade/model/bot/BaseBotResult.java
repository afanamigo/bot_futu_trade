package com.bottrade.model.bot;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class BaseBotResult extends BotInfo {

    private int code = 0;

    private String errorMsg;

}
