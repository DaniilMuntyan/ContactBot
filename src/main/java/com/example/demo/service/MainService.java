package com.example.demo.service;

import com.example.demo.botapi.ContactTelegramBot;
import com.example.demo.handlers.UpdateHandler;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Service
public class MainService {
    private static final Logger LOGGER = Logger.getLogger(MainService.class);

    private final UpdateHandler updateHandler;
    private final MessageService messageService;

    public MainService(UpdateHandler updateHandler, MessageService messageService) {
        LOGGER.info("MainService is creating...");
        this.updateHandler = updateHandler;
        this.messageService = messageService;
    }


    @Async // The main update handler
    public void mainHandler(Update update, ContactTelegramBot bot) {
        try {
            long start = System.currentTimeMillis();
            SendChatAction chatAction = null;
            if(update.hasMessage()) {
                chatAction = new SendChatAction();
                chatAction.setChatId(update.getMessage().getChatId().toString());
            }

            PartialBotApiMethod<?> responseApiMethod = updateHandler.updateHandler(update);
            long end = System.currentTimeMillis();
            executeApiMethod(update, responseApiMethod, chatAction, bot); // Depending on PartialApiMethod object type
            LOGGER.info("Computation time: " + (double)((end - start)) + " ms");
        } catch (TelegramApiException e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    private void executeApiMethod(Update update, PartialBotApiMethod<?> responseApiMethod, SendChatAction chatAction, ContactTelegramBot bot)
            throws TelegramApiException {
        if (update.hasCallbackQuery()) { // If callback is sent, then answer it
            bot.execute(getAnswerCallback(update.getCallbackQuery()));
        }
        if (responseApiMethod instanceof SendMessage) { // If bot going to send text message
            if (chatAction != null) {
                chatAction.setAction(ActionType.TYPING);
                bot.execute(chatAction);
            }
            SendMessage message = (SendMessage) responseApiMethod;
            String text = message.getText();
            if (isInvalidTextMessageLength(text)) { // If text is too long
                SendDocument sendDocument = documentFromText(text, update); // Pack it to the file and send
                if (sendDocument != null) {
                    bot.execute(sendDocument);
                }
            } else {
                bot.execute(message); // Or else send text message as usual
            }
        }
        if (responseApiMethod instanceof SendDocument) { // If bot going to send file
            if (chatAction != null) {
                chatAction.setAction(ActionType.UPLOADDOCUMENT);
                bot.execute(chatAction);
            }
            bot.execute((SendDocument) responseApiMethod);
        }
        if (responseApiMethod instanceof EditMessageText) { // If bot going to edit message (with buttons)
            if (chatAction != null) {
                chatAction.setAction(ActionType.TYPING);
                bot.execute(chatAction);
            }
            EditMessageText editMessageText = (EditMessageText) responseApiMethod;
            String text = editMessageText.getText();
            if (isInvalidTextMessageLength(text)) { // If text is too long
                SendDocument sendDocument = documentFromText(text, update); // Pack it to the file and edit
                if (sendDocument != null) {
                    bot.execute(sendDocument);
                }
            } else {
                bot.execute(editMessageText); // Or else edit text message as usual
            }
        }
    }

    // In order to take callback circle away from the button
    private AnswerCallbackQuery getAnswerCallback(CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(false);
        return answerCallbackQuery;
    }

    private boolean isInvalidTextMessageLength(String text) {
        return text.length() >= 4096;
    }

    private SendDocument documentFromText(String text, Update update) {
        try {
            return messageService.getFileWithText(text, update);
        } catch (IOException ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
        return null;
    }
}
