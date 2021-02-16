package com.example.demo.handlers;

import com.example.demo.botapi.ContactTelegramBot;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UpdateHandler {
    private static final Logger LOGGER = Logger.getLogger(UpdateHandler.class);

    private final MessageHandler messageHandler;

    @Autowired
    public UpdateHandler(MessageHandler messageHandler) {
        LOGGER.info("UpdateService is creating...");
        this.messageHandler = messageHandler;
    }

    // Main update handler
    public PartialBotApiMethod<?> updateHandler(Update update) {
        LOGGER.info("Update: " + update);

        // If text message has been received
        if (update.getMessage() != null && (update.getMessage().hasText() || update.getMessage().hasContact())) {
            PartialBotApiMethod<?> response = messageHandler.handleMessage(update.getMessage());

            return response;
        }
        return null;
    }
}