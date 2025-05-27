package com.bottrade.bot.model;

import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.TradeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Slf4j
public class BotMsgGenerator {


    public SendMessage buildInfoMsg(BotCmdResult<TradeInfo> infoBotCmdResult){
        log.info("[BotMsgGenerator] buildInfoMsg = "+ infoBotCmdResult);

        final String baseInfo = """
                💹
                *资产净值*: `%s`
                *现金*: `%s`
                *最大购买力*: `%s`
                *日内限额*: `%s`
                *持仓市值*：`%s`
                
                🅿️ *持仓列表*
                """;
        final String positionItemInfo = """
                ```%s
                              
                💹 名称：%s
                              
                🔢 数量：%s
                              
                💵 价格：%s
                
                🧩 均价：%s
                
                💰 市值：%s
                ```
                """;

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(infoBotCmdResult.getSourceChatId());
        if(infoBotCmdResult.getCode() != 0){
            buildErrorMsg(infoBotCmdResult,sendMessage,"获取账户信息出错！");
        }else {
            TradeInfo tradeInfo = infoBotCmdResult.getResult();
            StringBuilder textBuilder = new StringBuilder();
            textBuilder.append(String.format(baseInfo,
                    tradeInfo.getTotalAssets(),tradeInfo.getCash(),
                    tradeInfo.getPower(),tradeInfo.getDtbpAmount(),
                    tradeInfo.getMarketVal()
            ));

            if(tradeInfo.getPositions() == null || tradeInfo.getPositions().isEmpty()){
                textBuilder.append("\n").append("\uD83C\uDE33 暂无持仓！");
            }else {
                for (TradeInfo.Position position : tradeInfo.getPositions()){
                    textBuilder.append(String.format(positionItemInfo,
                            position.getCode(),
                            position.getName(),
                            position.getQty(),
                            position.getPrice(),
                            position.getCostPrice(),
                            position.getVal()
                            )).append("\n");
                }
            }

            sendMessage.setText(textBuilder.toString());
        }

        log.info("[BotMsgGenerator] sendMessage = "+ sendMessage);

        return sendMessage;
    }

    public SendMessage buildStringMsg(BotCmdResult<String> cmdResult){
        log.info("[BotMsgGenerator] buildAccListMsg = "+ cmdResult);
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(cmdResult.getSourceChatId());
        if(cmdResult.getCode() != 0){
            buildErrorMsg(cmdResult,sendMessage,"");
        }else {
            sendMessage.setText(cmdResult.getResult());
        }

        return sendMessage;
    }

    private SendMessage buildErrorMsg(BotCmdResult cmdResult,SendMessage sendMessage,String defMsg){
        sendMessage.setText("❌"+(StringUtils.isNotEmpty(cmdResult.getErrorMsg()) ? cmdResult.getErrorMsg() : defMsg));
        return sendMessage;
    }
}
