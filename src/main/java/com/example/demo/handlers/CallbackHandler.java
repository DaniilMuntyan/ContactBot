package com.example.demo.handlers;

import com.example.demo.botapi.ContactTelegramBot;
import com.example.demo.constants.ProgramVariables;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
@PropertySource("classpath:callback.properties")
public final class CallbackHandler {
    private static final Logger LOGGER = Logger.getLogger(CallbackHandler.class);

    private final AdminService adminService;
    private final ProgramVariables programVariables;
    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public CallbackHandler(AdminService adminService, ProgramVariables programVariables, UserService userService, MessageService messageService) {
        this.adminService = adminService;
        this.programVariables = programVariables;
        this.userService = userService;
        this.messageService = messageService;
    }

    private boolean checkUser(CallbackQuery callbackQuery) {
        Optional<User> user = userService.findByChatId(callbackQuery.getMessage().getChatId());
        if (user.isEmpty()) {
            return false;
        }
        return adminService.checkAuthentication(user.get());
    }

    public PartialBotApiMethod<?> handleCallback(CallbackQuery callbackQuery) {
        LOGGER.info("callbackQuery: " + callbackQuery);
        if (!checkUser(callbackQuery)) {
            return null;
        }

        String callbackData = callbackQuery.getData();
        if (callbackData.startsWith(programVariables.getDeleteCallbackYes())) {
            EditMessageText newMessage = new EditMessageText();
            newMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
            newMessage.setMessageId(callbackQuery.getMessage().getMessageId());

            String[] arrayCallbackData = callbackData.split(" ");
            if (arrayCallbackData.length != 2) {
                newMessage.setText(messageService.getDeleteFail());
                return newMessage;
            }

            String number = arrayCallbackData[1].trim();
            return adminService.deleteContactConfirmed(number, newMessage);
        }

        if(callbackData.equals(programVariables.getDeleteCallbackNo())) {
            EditMessageText newMessage = new EditMessageText();
            newMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
            newMessage.setMessageId(callbackQuery.getMessage().getMessageId());
            newMessage.setText(messageService.getDeleteCancel());
            LOGGER.info(newMessage);

            return newMessage;
        }

        return null;
    }
}
