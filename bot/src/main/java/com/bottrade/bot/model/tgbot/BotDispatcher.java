package com.bottrade.bot.model.tgbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.util.List;
import java.util.Objects;

@Component
public class BotDispatcher {

    @Autowired
    private List<LongPollingBot> pollingBots;

    public void execute(String botUsername, SendMessage sendMessage) throws TelegramApiException {
        for(LongPollingBot bot : pollingBots){
            if(Objects.equals(bot.getBotUsername(), botUsername)){
                if(bot instanceof AbsSender){
                    ((AbsSender) bot).execute(sendMessage);
                }
            }
        }
    }
}
