package com.bottrade.bot.model.tgbot;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.bottrade.bot.dao.service.impl.AtOriginalOrderService;
import com.bottrade.bot.dao.service.impl.AtTradeAccountService;
import com.bottrade.bot.model.mq.MQHelper;
import com.bottrade.bot.ocr.OcrOrderInfo;
import com.bottrade.bot.ocr.OcrService;
import com.bottrade.bot.utils.RedisUtil;
import com.bottrade.model.OrderUpdateDTO;
import com.bottrade.model.OriginalOrderDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Johnny.L
 * @date 2023/12
 * <p>
 * 微牛订单截图文字识别
 * bot: stock_oracle_bot
 */
@Component
@Slf4j
public class OrderNotifyBot extends TelegramLongPollingBot {

    //配置文件 -> to DB admin user
    private static final List<String> SOURCE_MESSAGES = Arrays.asList("dayunddd","afanamigo","test_stock_oracle","test_stock_oracle_channel","test_stock_oracle_bot", "stock_oracle_bot", "StockOracleBot");
    /**
     * tg 机器人密钥配置
     */
    private static final String token = "6115826154:AAFJ26fahxpwR4r748umJooDC6ETVST8iX8";
    private static final String botUsername = "StockOracleBot";
    //备用bot
//    private static final String token = "6848006739:AAHFyUUPkt-XAuD1q3QFqMGZ5NG6_6FxhE0";
//    private static final String botUsername = "stock_lab_oracle_bot";
    private static final String ORDER_SPLIT_INDEX = "symbol";

    @Autowired
    private OcrService ocrService;

    @Autowired
    private MQHelper mqHelper;

    @Autowired
    private AtOriginalOrderService originalOrderService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AtTradeAccountService atTradeAccountService;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        DateTime nowDate = DateUtil.date();
        Message message;
        if (null == update.getMessage()) {
            message = update.getChannelPost();
        } else {
            message = update.getMessage();
        }
        Long chatId = message.getChatId();
        SendMessage sendMsg = new SendMessage();
        sendMsg.setChatId(chatId);
        log.info("++===== Stock Oracle ++===== 收到新消息[图片识别]：");
        log.info("++===== Stock Oracle ++===== 发送方：{}, 频道消息类型：{}, 包含图片: {}",
                message.getChat().getTitle(), message.isChannelMessage(), message.hasPhoto());
        try {
            //限定频道消息 //指定频道id getUserName(发送者id) getTitle(发送者名称)
            if (!(SOURCE_MESSAGES.contains(message.getChat().getUserName()) || SOURCE_MESSAGES.contains(message.getChat().getTitle()))) {
                sendMsg.setText("++===== 未知消息类型或未认证消息来源");
                execute(sendMsg);
                return;
            }
            if (!message.hasPhoto()) {
                sendMsg.setText("++===== 仅支持识别WeBull订单截图");
                execute(sendMsg);
                return;
            }

            log.info("++===== order notify bot received message, has photo: {}", message.hasPhoto());

            List<PhotoSize> photos = message.getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);
            String fileId = photo.getFileId();
            //Get the file object from Telegram
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            File file = execute(getFile);

            // Get the file URL
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
            log.info("++===== download photo: {}", fileUrl);

            sendMsg.setChatId(chatId);
            sendMsg.setText("收到图片"+fileUrl+",正在处理中，请稍后...");
            execute(sendMsg);

            //get file stream -> OCR txt
            log.info("++===== recognizeText: start");
            OcrOrderInfo ocrOrderInfo = ocrService.aiRecognizeText(fileUrl);

            if (ocrOrderInfo == null) {
                sendMsg.setText("仅支持识别WeBull订单截图 -- 未识别订单信息");
                execute(sendMsg);
                return;
            }



            //read orders
            String batchId = getBatchId();
            log.info("++===== getBatchId: {}", batchId);
            Integer nextBatchIndex = originalOrderService.getNextBatchIndex();//-> todo 线性订单批次记录[简版忽略]
            log.info("++===== getNextBatchIndex: {}", nextBatchIndex);

            OrderUpdateDTO orderUpdateDTO = ocrOrderInfo.convertToDTO(batchId,nextBatchIndex);
            orderUpdateDTO.setSourceChatId(chatId);
            orderUpdateDTO.setBotUsername(getBotUsername());

            sendMsg.setText("Order Info: " + System.lineSeparator() + new Gson().toJson(ocrOrderInfo));

            execute(sendMsg);
            //识别消息类型 -> 图片 -> 文字 -> MQ 下发指令
            handleOrder(orderUpdateDTO);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


    public void handleOrder(OrderUpdateDTO orderUpdateDTO) {
        List<OriginalOrderDTO> orderDTOS = orderUpdateDTO.getOrders();
        if(null == orderDTOS || orderDTOS.size()==0){
            //调用清仓
            originalOrderService.sendOrderUpdate(new HashMap<>());
            return;
        }
        //主账户资金存入缓存
//        if(StringUtils.isEmpty(redisUtil.getCacheObject(atTradeAccountService.getAccountValKey()))){
//            redisUtil.setCacheObject(atTradeAccountService.getAccountValKey(),orderUpdateDTO.getAccountVal().toPlainString());
//        }
//        BigDecimal netVal = new BigDecimal(redisUtil.getCacheObject(atTradeAccountService.getAccountValKey()).toString());
        BigDecimal netVal = orderUpdateDTO.getAccountVal();

        //查询下一订单批次
        Integer batchIndex = orderDTOS.get(0).getOrderBatchIndex();
        //保存的新批次原订单
        Boolean saved = originalOrderService.saveBatchOrders(orderDTOS);

        // 订单保存成功 -> 分析订单变化
        if(saved){
            log.info("消费端Payload: " + orderDTOS);
            originalOrderService.compareLastBatch(batchIndex,netVal);
        }
    }

    public String getBatchId() {
        DateTime nowDate = DateUtil.date();
        return DateUtil.formatDateTime(nowDate).replace(" ", "_").replace(":", "_");
    }
}
