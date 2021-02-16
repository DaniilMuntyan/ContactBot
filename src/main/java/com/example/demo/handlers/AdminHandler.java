package com.example.demo.handlers;

import com.example.demo.constants.Commands;
import com.example.demo.model.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
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
        if(text.strip().equals(Commands.ADMIN)) { // If password has not been written after /admin
            return null;
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
        String[] arrayString = text.strip().split(" ");
        String command = arrayString[0];
        String[] stringPart = Arrays.copyOfRange(arrayString, 1, arrayString.length);
        String stringWithoutCommand = String.join(" ", stringPart).strip();
        PartialBotApiMethod<?> answer = null;

        switch(command) {
            case Commands.ADD:
                answer = adminService.addContact(stringWithoutCommand, response);
                break;
            case Commands.EDIT:
                answer = adminService.editContact(stringWithoutCommand, response);
                break;
            case Commands.DELETE:
                answer = adminService.deleteContact(stringWithoutCommand, response);
                break;
            case Commands.LIST:
                try {
                    answer = adminService.listNewContacts(response);
                } catch (IOException ex) {
                    LOGGER.error(ex);
                    ex.printStackTrace();
                }
                break;
            default:
                break;
        }
        //response.setText(answer);
        return answer;
    }

}
