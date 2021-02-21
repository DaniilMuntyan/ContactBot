package com.example.demo.handlers;

import com.example.demo.constants.Commands;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.util.Arrays;

@Component
public final class AdminHandler {
    private static final Logger LOGGER = Logger.getLogger(AdminHandler.class);

    private final AdminService adminService;
    private final MessageService messageService;
    private final UserService userService;


    public AdminHandler(AdminService adminService, MessageService messageService, UserService userService) {
        LOGGER.info("AdminHandler is creating...");
        this.adminService = adminService;
        this.messageService = messageService;
        this.userService = userService;
    }

    public PartialBotApiMethod<?> handleAuthentication(String text, SendMessage response, User user) {
        if(user.isAdminMode()) {
            response.setText(messageService.getAlreadyAdmin());
            return response;
        }

        String password = adminService.getPasswordFromMessage(text);

        if(adminService.checkAuthentication(password)) { // If password is right
            response.setText(messageService.getHelloAdmin());
            user.setAdminMode(true);
            userService.editAdminMode(user, true);
        } else {
            response.setText(messageService.getWrongAdminPassword());
        }

        return response;
    }

    public PartialBotApiMethod<?> handleAdminCommand(String text, User user, SendMessage response) {
        String[] arrayString = text.trim().split(" ");
        String command = arrayString[0];
        String[] stringPart = Arrays.copyOfRange(arrayString, 1, arrayString.length);
        String stringWithoutCommand = String.join(" ", stringPart).trim();
        PartialBotApiMethod<?> answer = null;

        switch(command) {
            case Commands.ADD:
                answer = adminService.addContact(stringWithoutCommand, response, user);
                break;
            case Commands.EDIT:
                answer = adminService.editContact(stringWithoutCommand, response, user);
                break;
            case Commands.DELETE:
                answer = adminService.deleteContactQuestion(stringWithoutCommand, response);
                break;
            case Commands.STAT:
                LOGGER.info("stringWithoutCommand: " + stringWithoutCommand);
                answer = adminService.stat(stringWithoutCommand, response);
                break;
            default:
                break;
        }

        switch(text) {
            case Commands.NEW:
                try {
                    answer = adminService.listNewContacts(response);
                } catch (IOException e) {
                    LOGGER.error(e);
                    e.printStackTrace();
                }
                break;
            case Commands.BACKUP:
                try {
                    answer = adminService.backup(response);
                } catch (IOException e) {
                    LOGGER.error(e);
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return answer;
    }

}
