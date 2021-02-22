package com.example.demo.service.commands;

import com.example.demo.model.User;
import com.example.demo.service.MessageService;
import com.example.demo.service.model.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class AuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class);
    private final MessageService messageService;
    private final UserService userService;

    @Value("${ADMIN_PASSWORD}")
    private String password;

    @Autowired
    public AuthenticationService(MessageService messageService, UserService userService) {
        LOGGER.info("AuthenticationService is creating...");
        this.messageService = messageService;
        this.userService = userService;
    }

    public PartialBotApiMethod<?> handleAuthentication(String text, SendMessage response, User user) {
        if(user.isAdminMode()) {
            response.setText(messageService.getAlreadyAdmin());
            return response;
        }

        String password = this.getPasswordFromMessage(text);

        if(this.checkAuthentication(password)) { // If password is right
            response.setText(messageService.getHelloAdmin());
            user.setAdminMode(true);
            userService.editAdminMode(user, true);
        } else {
            response.setText(messageService.getWrongAdminPassword());
        }

        return response;
    }

    private String getPasswordFromMessage(String text) {
        String[] arrayString = text.split(" ");
        return arrayString[1].trim();
    }

    private boolean checkAuthentication(String text) {
        return this.password.trim().equals(text.trim());
    }
}
