package com.example.demo.botapi;

import com.example.demo.service.MainService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@PropertySource("classpath:application.properties")
@RestController
public class ContactTelegramBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = Logger.getLogger(ContactTelegramBot.class);

    private final MainService mainService;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public ContactTelegramBot(MainService mainService) {
        LOGGER.info("ContactTelegramBot is creating...");
        this.mainService = mainService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        mainService.mainHandler(update, this);
    }
}
