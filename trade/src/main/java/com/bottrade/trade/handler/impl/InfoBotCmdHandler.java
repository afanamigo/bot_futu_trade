package com.bottrade.trade.handler.impl;

import com.bottrade.model.bot.BotCmdInfo;
import com.bottrade.model.Constant;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.TradeInfo;
import com.bottrade.trade.handler.IBotCmdHandler;
import com.bottrade.trade.model.moomoo.MoomooClient;
import com.bottrade.trade.model.mq.MQHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class InfoBotCmdHandler implements IBotCmdHandler {

    @Autowired
    private MoomooClient moomooClient;

    @Autowired
    private MQHelper mqHelper;

    @Override
    public String cmd() {
        return Constant.BotCmd.info.getCmd();
    }

    @Override
    public void handle(BotCmdInfo cmdInfo) {
        CompletableFuture<TradeInfo> future = moomooClient.getInfo();
        future.whenComplete((tradeInfo, throwable) -> {
            BotCmdResult<TradeInfo> cmdResult = new BotCmdResult<>();
            cmdResult.setBotUsername(cmdInfo.getBotUsername());
            cmdResult.setSourceChatId(cmdInfo.getSourceChatId());
            cmdResult.setCmd(cmdInfo.getCmd());
            if(throwable != null){
                cmdResult.setCode(-1);
                cmdResult.setErrorMsg(throwable.toString());
            }else {
                try {
                    cmdResult.setCode(0);
                    cmdResult.setResult(future.get());
                } catch (Exception e) {
                    cmdResult.setCode(-1);
                    cmdResult.setErrorMsg(e.toString());
                }
                mqHelper.notifyCmdResult(cmdResult);
            }
        });
    }
}
