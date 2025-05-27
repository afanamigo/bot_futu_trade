package com.bottrade.trade.handler.impl;

import com.alibaba.fastjson.JSON;
import com.bottrade.model.Constant;
import com.bottrade.model.bot.BotCmdInfo;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.utils.Utils;
import com.bottrade.trade.handler.IBotCmdHandler;
import com.bottrade.trade.model.moomoo.MoomooAttributes;
import com.bottrade.trade.model.moomoo.MoomooClient;
import com.bottrade.trade.model.mq.MQHelper;
import com.moomoo.openapi.pb.TrdGetAccList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class SetupBotCmdHandler implements IBotCmdHandler {

    @Autowired
    private MoomooClient moomooClient;

    @Autowired
    private MQHelper mqHelper;

    @Autowired
    private MoomooAttributes moomooAttributes;

    @Override
    public String cmd() {
        return Constant.BotCmd.setup.getCmd();
    }

    @Override
    public void handle(BotCmdInfo cmdInfo) {
        BotCmdResult<String> cmdResult = new BotCmdResult<>(cmdInfo);
        cmdResult.setCmd(cmdInfo.getCmd());
        Map<String,String> extraMap = Utils.fromCmdExtra(cmdInfo.getExtra());
        log.info("[SetupBotCmdHandler] extraMap = " + extraMap);
        if(extraMap.size() > 0){
            moomooAttributes.copyFromMap(extraMap);
            moomooClient.close();
            cmdResult.setCode(0);
            cmdResult.setResult(moomooAttributes.toString());
        }else {
            cmdResult.setCode(-1);
            cmdResult.setErrorMsg("错误的配置！！");
        }
        mqHelper.notifyCmdResult(cmdResult);
    }
}
