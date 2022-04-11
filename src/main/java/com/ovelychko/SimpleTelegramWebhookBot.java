package com.ovelychko;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class SimpleTelegramWebhookBot extends TelegramWebhookBot {

    private final String webHookPath = "";
    private final String userName = "";
    private final String botToken = "";
    private final String movieSearchLink = "";

    public SimpleTelegramWebhookBot() {
        log.info("WebhookTelegramController created");
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        System.out.println("onUpdateReceived");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("Message received!");
        try {
            this.execute(sendMessage);
            log.info("Message sent");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.info("Message sent Failed");
        }
        return null;
    }

    public String getBotUsername() {
        return userName;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotPath() {
        return webHookPath;
    }
}
