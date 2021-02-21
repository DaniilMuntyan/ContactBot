package com.example.demo.handlers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateHandler {
    private static final Logger LOGGER = Logger.getLogger(UpdateHandler.class);

    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;

    @Autowired
    public UpdateHandler(MessageHandler messageHandler, CallbackHandler callbackHandler) {
        LOGGER.info("UpdateService is creating...");
        this.callbackHandler = callbackHandler;
        this.messageHandler = messageHandler;
    }

    public PartialBotApiMethod<?> updateHandler(Update update) {
        PartialBotApiMethod<?> response = null;
        if (update.getMessage() != null && (update.getMessage().hasText() || update.getMessage().hasContact())) {
            response = messageHandler.handleMessage(update.getMessage()); // If text message has been received
        } else {
            if (update.hasCallbackQuery()) {
                response = callbackHandler.handleCallback(update.getCallbackQuery()); // If callback has been received
            }
        }
        return response;
    }
}
