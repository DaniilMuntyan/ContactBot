package com.example.demo.handlers;

import com.example.demo.service.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
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

    // Main update handler
    public PartialBotApiMethod<?> updateHandler(Update update) {
        LOGGER.info("Update: " + update);
        PartialBotApiMethod<?> response = null;
        // If text message has been received
        if (update.getMessage() != null && (update.getMessage().hasText() || update.getMessage().hasContact())) {
            response = messageHandler.handleMessage(update.getMessage());
        } else {
            // If callback has been received
            if (update.hasCallbackQuery()) {
                response = callbackHandler.handleCallback(update.getCallbackQuery());
            }
        }
        return response;
    }
}
