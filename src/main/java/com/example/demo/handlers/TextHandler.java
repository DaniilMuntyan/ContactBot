package com.example.demo.handlers;

import com.example.demo.model.Phone;
import com.example.demo.model.User;
import com.example.demo.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.Optional;

@Component
public final class TextHandler {
    private static final Logger LOGGER = Logger.getLogger(TextHandler.class);

    private final MessageService messageService;
    private final NewContactService newContactService;
    private final PhoneService phoneService;
    private final UserService userService;

    @Autowired
    public TextHandler(MessageService messageService, NewContactService newContactService, PhoneService phoneService, UserService userService) {
        LOGGER.info("TextHandler is creating...");
        this.userService = userService;
        this.phoneService = phoneService;
        this.messageService = messageService;
        this.newContactService = newContactService;
    }

    // Received phone number
    public PartialBotApiMethod<?> handleText(String text, SendMessage response, User user) {
        LOGGER.info(String.format("handleText: %s", text));
        Optional<Phone> phone = phoneService.findContact(text);

        if (phone.isEmpty()) {
            newContactService.saveNewContact(text);
            response.setText(messageService.getNotFoundMessage());
        } else {
            response.setText(phone.get().getName());
        }

        if(user.isAdminMode()) { // If admin sends text to the bot, he/she will be exited from admin mode
            user.setAdminMode(false);
            userService.editAdminMode(user, false);
            LOGGER.info("User " + user.getId() + " exited from admin mode");
        }

        return response;
    }

    public PartialBotApiMethod<?> handleContact(Contact contact, User user, SendMessage response) {
        LOGGER.info(String.format("New contact: %s from User: %s", contact, user));

        if (!user.isAdminMode()) {
            return null;
        }

        phoneService.saveContact(contact);
        response.setText(messageService.getAddSuccess());
        return response;
    }
}
