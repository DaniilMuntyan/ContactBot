package com.example.demo.botapi;

import com.example.demo.handlers.UpdateHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@PropertySource("classpath:application.properties")
/*public class ContactTelegramBot extends TelegramWebhookBot {
    private static final Logger LOGGER = Logger.getLogger(ContactTelegramBot.class);

    private final UpdateService updateService;

    @Value("${bot.webhook}")
    private String webhookPath;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public ContactTelegramBot(UpdateService updateService) {
        LOGGER.info("ContactTelegramBot is creating...");
        this.updateService = updateService;
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
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return updateService.updateHandler(this, update);
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }
}*/
public class ContactTelegramBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = Logger.getLogger(ContactTelegramBot.class);

    private final UpdateHandler updateHandler;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public ContactTelegramBot(UpdateHandler updateHandler) {
        LOGGER.info("ContactTelegramBot is creating...");
        this.updateHandler = updateHandler;
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
            execute((SendMessage) responseApiMethod);
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
            execute((EditMessageText) responseApiMethod);
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
}
