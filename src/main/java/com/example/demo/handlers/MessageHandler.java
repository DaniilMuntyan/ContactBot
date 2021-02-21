package com.example.demo.handlers;

import com.example.demo.constants.Commands;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
public final class MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class);

    private final UserService userService;
    private final TextHandler textHandler;
    private final CommandHandler commandHandler;

    private final List<String> commands;

    public MessageHandler(UserService userService, TextHandler textHandler, CommandHandler commandHandler) {
        LOGGER.info("MessageHandler is creating...");
        this.userService = userService;
        this.textHandler = textHandler;
        this.commandHandler = commandHandler;
        commands = Commands.getAllCommands();
    }

    public PartialBotApiMethod<?> handleMessage(Message message) {
        final Integer messageId = message.getMessageId();
        final String text = message.getText();
        final Long chatId = message.getChatId();
        //LOGGER.info(String.format("handleMessage: %s %s", chatId.toString(), text));

        Optional<User> user = userService.findByChatId(chatId);
        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setReplyToMessageId(messageId);

        if (user.isEmpty()) { // If user is new
            User newUser = userService.getUserFromMessage(message);
            user = Optional.of(userService.saveUser(newUser));
        } else {
            if (userService.checkDdos(user.get())) {
                return null;
            }
        }

        if(message.hasContact()) { // If phone contact has been received
            return textHandler.handleContact(message.getContact(), user.get(), response);
        }

        if (checkIfCommand(text)) { // If command was sent
            return commandHandler.handleCommand(text, user.get(), response); // If command
        } else { // If text was sent
            return textHandler.handleText(text, response, user.get()); // If text
        }
    }

    private boolean checkIfCommand(String text) {
        for(String cmd: commands) {
            if (text.contains(cmd)) {
                return true;
            }
        }
        return false;
    }
}
