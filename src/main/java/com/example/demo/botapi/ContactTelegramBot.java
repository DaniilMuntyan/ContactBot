package com.example.demo.botapi;

import com.example.demo.handlers.UpdateHandler;
import com.example.demo.service.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

//@Component
@PropertySource("classpath:application.properties")
@RestController
/*public class ContactTelegramBot extends TelegramWebhookBot {
    private static final Logger LOGGER = Logger.getLogger(ContactTelegramBot.class);

    private final UpdateHandler updateHandler;

    private final MessageService messageService;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.webhook}")
    private String webhookPath;

    @Autowired
    public ContactTelegramBot(UpdateHandler updateHandler, MessageService messageService) {
        LOGGER.info("ContactTelegramBot is creating...");
        this.updateHandler = updateHandler;
        this.messageService = messageService;
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
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            SendChatAction chatAction = null;
            if (update.hasMessage()) {
                chatAction = new SendChatAction();
                chatAction.setChatId(update.getMessage().getChatId().toString());
            }
            PartialBotApiMethod<?> resultApiMethod = updateHandler.updateHandler(update);
            return responseApiMethod(update, resultApiMethod, chatAction);
        } catch (TelegramApiException ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
        return null;
    }

    private BotApiMethod<?> responseApiMethod(Update update, PartialBotApiMethod<?> responseApiMethod, SendChatAction chatAction)
            throws TelegramApiException {
        if (responseApiMethod instanceof SendMessage) { // If bot going to send text message
            if (chatAction != null) {
                chatAction.setAction(ActionType.TYPING);
                execute(chatAction);
            }
            if (update.hasCallbackQuery()) { // If callback is sent, then answer it
                answerCallback(update.getCallbackQuery());
            }

            SendMessage message = (SendMessage) responseApiMethod;
            String text = message.getText();
            if (!isValidTextMessageLength(text)) { // If text is too long
                SendDocument sendDocument = documentFromText(text, update); // Pack it to the file and send
                if (sendDocument != null) {
                    execute(sendDocument);
                    return null;
                }
            } else {
                return message; // Or else send text message as usual
            }
        }
        if (responseApiMethod instanceof SendDocument) { // If bot going to send file
            if (chatAction != null) {
                chatAction.setAction(ActionType.UPLOADDOCUMENT);
                execute(chatAction);
            }
            if (update.hasCallbackQuery()) { // If callback is sent, then answer it
                answerCallback(update.getCallbackQuery());
            }
            execute((SendDocument) responseApiMethod);
            return null;
        }
        if (responseApiMethod instanceof EditMessageText) { // If bot going to edit message (with buttons)
            if (chatAction != null) {
                chatAction.setAction(ActionType.TYPING);
                execute(chatAction);
            }
            if (update.hasCallbackQuery()) { // If callback is sent, then answer it
                answerCallback(update.getCallbackQuery());
            }
            EditMessageText editMessageText = (EditMessageText) responseApiMethod;
            String text = editMessageText.getText();
            if (!isValidTextMessageLength(text)) { // If text is too long
                SendDocument sendDocument = documentFromText(text, update);
                if (sendDocument != null) {
                    execute(sendDocument);
                }
                return null;
            } else {
                return editMessageText; // Or else edit text message as usual
            }
        }
        return null;
    }

    // In order to take callback circle away from the button
    private void answerCallback(CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(false);
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }

    private boolean isValidTextMessageLength(String text) {
        return text.length() < 4096;
    }

    private SendDocument documentFromText(String text, Update update) {
        try {
            return messageService.getFileFromMessage(text, update);
        } catch (IOException ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
        return null;
    }
}*/
public class ContactTelegramBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = Logger.getLogger(ContactTelegramBot.class);

    private final UpdateHandler updateHandler;

    private final MessageService messageService;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public ContactTelegramBot(UpdateHandler updateHandler, MessageService messageService) {
        LOGGER.info("ContactTelegramBot is creating...");
        this.updateHandler = updateHandler;
        this.messageService = messageService;
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
        try {
            SendChatAction chatAction = null;
            if(update.hasMessage()) {
                chatAction = new SendChatAction();
                chatAction.setChatId(update.getMessage().getChatId().toString());
            }

            PartialBotApiMethod<?> responseApiMethod = updateHandler.updateHandler(update);
            executeApiMethod(update, responseApiMethod, chatAction); // Depending on PartialApiMethod object type

        } catch (TelegramApiException e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    private void executeApiMethod(Update update, PartialBotApiMethod<?> responseApiMethod, SendChatAction chatAction)
            throws TelegramApiException {
        if (responseApiMethod instanceof SendMessage) { // If bot going to send text message
            if (chatAction != null) {
                chatAction.setAction(ActionType.TYPING);
                execute(chatAction);
            }
            if (update.hasCallbackQuery()) { // If callback is sent, then answer it
                answerCallback(update.getCallbackQuery());
            }

            SendMessage message = (SendMessage) responseApiMethod;
            String text = message.getText();
            if (!isValidTextMessageLength(text)) { // If text is too long
                SendDocument sendDocument = documentFromText(text, update); // Pack it to the file and send
                if (sendDocument != null) {
                    execute(sendDocument);
                }
            } else {
                execute(message); // Or else send text message as usual
            }
        }
        if (responseApiMethod instanceof SendDocument) { // If bot going to send file
            if (chatAction != null) {
                chatAction.setAction(ActionType.UPLOADDOCUMENT);
                execute(chatAction);
            }
            if (update.hasCallbackQuery()) { // If callback is sent, then answer it
                answerCallback(update.getCallbackQuery());
            }
            execute((SendDocument) responseApiMethod);
        }
        if (responseApiMethod instanceof EditMessageText) { // If bot going to edit message (with buttons)
            LOGGER.info("EDIT MESSAGE TEXT");
            if (chatAction != null) {
                chatAction.setAction(ActionType.TYPING);
                execute(chatAction);
            }
            if (update.hasCallbackQuery()) { // If callback is sent, then answer it
                answerCallback(update.getCallbackQuery());
            }
            EditMessageText editMessageText = (EditMessageText) responseApiMethod;
            String text = editMessageText.getText();
            if (!isValidTextMessageLength(text)) { // If text is too long
                SendDocument sendDocument = documentFromText(text, update); // Pack it to the file and edit
                if (sendDocument != null) {
                    execute(sendDocument);
                }
            } else {
                execute(editMessageText); // Or else edit text message as usual
            }
        }
    }

    // In order to take callback circle away from the button
    private void answerCallback(CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(false);
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
    }

    private boolean isValidTextMessageLength(String text) {
        return text.length() < 4096;
    }

    private SendDocument documentFromText(String text, Update update) {
        try {
            return messageService.getFileFromMessage(text, update);
        } catch (IOException ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        }
        return null;
    }
}
