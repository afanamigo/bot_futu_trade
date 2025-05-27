package com.bottrade.bot.config;

import com.bottrade.bot.model.tgbot.OrderNotifyBot;
import com.bottrade.bot.model.tgbot.OrderNotifyBotTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TgBotConf {

    @Autowired
    @Lazy
    private OrderNotifyBot orderNotifyBot;

    @Autowired
    @Lazy
    private OrderNotifyBotTest orderNotifyBotTest;

    /**
     * 注册tg机器人: stock_oracle_bot
     * @return
     */
    @Bean
    public TelegramBotsApi telegramBotApi(){
        TelegramBotsApi telegramBotApi;
        try {
            telegramBotApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotApi.registerBot(orderNotifyBot);
            telegramBotApi.registerBot(orderNotifyBotTest);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return telegramBotApi;
    }
}
