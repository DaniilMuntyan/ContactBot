package com.example.demo.handlers;

import com.example.demo.constants.Commands;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.MessageService;
import com.example.demo.service.model.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public final class CommandHandler {
    private static final Logger LOGGER = Logger.getLogger(CommandHandler.class);

    private final MessageService messageService;
    private final AdminHandler adminHandler;
    private final AdminService adminService;
    private final UserService userService;

    @Autowired
    public CommandHandler(MessageService messageService, AdminHandler adminHandler, AdminService adminService, UserService userService) {
        LOGGER.info("CommandHandler is creating...");
        this.userService = userService;
        this.messageService = messageService;
        this.adminHandler = adminHandler;
        this.adminService = adminService;
    }

    public PartialBotApiMethod<?> handleCommand(String text, User user, SendMessage response) {
        LOGGER.info(String.format("handleCommand: %s %s", user.getChatId(), text));
        if((text.startsWith(Commands.ADMIN) && messageService.countWords(text) == 2)) {
            return adminService.handleAuthentication(text, response, user);
        }

        switch(text) {
            case Commands.START:
                response.setText(messageService.getStartMessage());
                if (user.isAdminMode()) { // If admin sends /admin, he/she will be exited from admin mode
                    user.setAdminMode(false);
                    userService.editAdminMode(user, false);
                }
                return response;
            case Commands.HELP:
            case Commands.ADMIN: // One case for /help and /admin
                if (user.isAdminMode()) {
                    response.setText(messageService.getHelloAdmin());
                } else {
                    response.setText(messageService.getHelpMessage());
                }
                return response;
            default:
                break;
        }

        boolean isAdminCommand = (text.startsWith(Commands.ADD) ||
                text.startsWith(Commands.EDIT) ||
                text.startsWith(Commands.DELETE) ||
                text.startsWith(Commands.NEW) ||
                text.startsWith(Commands.BACKUP) ||
                text.startsWith(Commands.STAT));
        if (isAdminCommand) { // If command has been received
            if (user.isAdminMode()) {
                return adminHandler.handleAdminCommand(text, user, response);
            } else {
                return messageService.getYouAreNotAdmin(response);
            }
        }
        return null;
    }
}
