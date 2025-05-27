package com.bottrade.bot.model.tgbot;

import com.bottrade.bot.dao.OrderConvertUtil;
import com.bottrade.bot.model.mq.MQHelper;
import com.bottrade.bot.ocr.OcrService;
import com.bottrade.model.bot.BotCmdInfo;
import com.bottrade.model.Constant;
import com.bottrade.model.OrderDTO;
import com.bottrade.model.OrderUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Johnny.L
 * @date 2023/12
 * <p>
 * 微牛订单截图文字识别
 */
@Component
@Slf4j
public class OrderNotifyBotTest extends TelegramLongPollingBot {
    //配置文件 -> to DB admin user
    private static final List<String> SOURCE_MESSAGES = Arrays.asList("afanamigo","dayunddd");

    /**
     * tg 机器人密钥配置
     */
    private static final String token = "6988271841:AAH45muQyuk7NKxENkYrgKl96i3O8wHp_9I";
    private static final String botUsername = "test_stock_oracle_bot";
//    备用bot
//    private static final String token = "6743375621:AAHQpFasNvmGCK19odIjqmq_SCYt48m8NH8";
//    private static final String botUsername = "test_stock_lab_oracle_bot";
    private static final String ORDER_SPLIT_INDEX = "symbol";
//    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private OcrService ocrService;

    @Autowired
    private MQHelper mqHelper;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    /**
     * 测试bot 手动下单
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {

        //order test by [symbol, qty] ; [symbol, qty, price]  分号(;)、换行 间隔，没有[]
        Message message;
        if (null == update.getMessage()) {
            message = update.getChannelPost();
        } else {
            message = update.getMessage();
        }
        Long chatId = message.getChatId();
        SendMessage sendMsg = new SendMessage();
        sendMsg.setChatId(chatId);
        log.info("++===== Stock Oracle ++===== 收到新消息[指定订单]：");
        try {
            //限定频道消息 //指定频道id getUserName(发送者id) getTitle(发送者名称)
            if (null == message || !SOURCE_MESSAGES.contains(message.getChat().getUserName())) {
                sendMsg.setText("++===== 未知消息类型或未认证消息来源");
                execute(sendMsg);
                return;
            }

            String text = message.getText();

            if(handleCommand(chatId,text)){
                return;
            }

            OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO();
            List<OrderDTO> orderList = new ArrayList<>();


            log.info("++===== [指定订单] ++===== ：{}", text);

            String[] orderArr = text.replace("\r\n|\r|\n","").replace(" ","").split(";");

            for (String str : orderArr) {
                String[] orderStr = str.split(",");
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setQty(Integer.parseInt(orderStr[1]));
                orderDTO.setSymbol(orderStr[0]);
                orderDTO.setSymbolFlag(orderStr[0] + "_" + OrderConvertUtil.getTradeType(orderDTO.getQty()));
                if(orderStr.length>2){
                    orderDTO.setLastPrice(new BigDecimal(orderStr[2]).setScale(4, RoundingMode.UP));
                }
                orderList.add(orderDTO);
            }

            orderUpdateDTO.setSimpleOrders(orderList);

            orderUpdateDTO.setSourceChatId(chatId);
            orderUpdateDTO.setBotUsername(getBotUsername());

            mqHelper.publishSimpleOrder(orderUpdateDTO);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean handleCommand(Long chatId,String text) {//查询指令
        Constant.BotCmd botCmd = null;
        String[] originCmds = text.split(" ");
        if(originCmds.length > 0){
            botCmd = Constant.BotCmd.of(originCmds[0]);
        }
        if(botCmd != null){
            BotCmdInfo cmdInfo = new BotCmdInfo();
            cmdInfo.setCmd(botCmd.getCmd());
            if(originCmds.length > 1){
                cmdInfo.setExtra(text.replace(botCmd.getCmd()+" ",""));
            }
            cmdInfo.setBotUsername(getBotUsername());
            cmdInfo.setSourceChatId(chatId);
            mqHelper.publishCmd(cmdInfo);
            return true;
        }
        return false;
    }

}
