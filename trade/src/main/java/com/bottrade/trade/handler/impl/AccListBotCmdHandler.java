package com.bottrade.trade.handler.impl;

import com.bottrade.model.Constant;
import com.bottrade.model.bot.BotCmdInfo;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.TradeInfo;
import com.bottrade.model.utils.Utils;
import com.bottrade.trade.handler.IBotCmdHandler;
import com.bottrade.trade.model.moomoo.MoomooClient;
import com.bottrade.trade.model.mq.MQHelper;
import com.moomoo.openapi.pb.TrdGetAccList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AccListBotCmdHandler implements IBotCmdHandler {

    @Autowired
    private MoomooClient moomooClient;

    @Autowired
    private MQHelper mqHelper;

    @Override
    public String cmd() {
        return Constant.BotCmd.accList.getCmd();
    }

    @Override
    public void handle(BotCmdInfo cmdInfo) {
        CompletableFuture<TrdGetAccList.Response> future = moomooClient.getAccList();
        future.whenComplete((tradeInfo, throwable) -> {
            BotCmdResult<String> cmdResult = new BotCmdResult<>();
            cmdResult.setBotUsername(cmdInfo.getBotUsername());
            cmdResult.setSourceChatId(cmdInfo.getSourceChatId());
            cmdResult.setCmd(cmdInfo.getCmd());
            if(throwable != null){
                cmdResult.setCode(-1);
                cmdResult.setErrorMsg(throwable.toString());
            }else {
                try {
                    TrdGetAccList.Response response = future.get();
                    cmdResult.setCode(response.getRetType());
                    if(cmdResult.getCode() != 0){
                        cmdResult.setResult(Utils.decodeRetMsg(response.getRetMsg()));
                    }else {
                        cmdResult.setResult(Utils.decodeRetMsg(response.getS2C().toString()));
                    }
                } catch (Exception e) {
                    cmdResult.setCode(-1);
                    cmdResult.setErrorMsg(e.toString());
                }
                mqHelper.notifyCmdResult(cmdResult);
            }
        });
    }
}
