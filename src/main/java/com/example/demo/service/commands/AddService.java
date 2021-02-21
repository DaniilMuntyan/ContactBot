package com.example.demo.service.commands;

import com.example.demo.model.User;
import com.example.demo.service.MessageService;
import com.example.demo.service.model.PhoneService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
public class AddService {
    private static final Logger LOGGER = Logger.getLogger(AddService.class);

    private final MessageService messageService;
    private final PhoneService phoneService;

    @Autowired
    public AddService(MessageService messageService, PhoneService phoneService) {
        LOGGER.info("AddService is creating...");
        this.messageService = messageService;
        this.phoneService = phoneService;
    }

    public SendMessage addContact(String message, SendMessage response, User creator) {
        List<String> phoneAndName = messageService.getPhoneAndName(message);

        if(phoneAndName == null) {
            response.setText(messageService.getWrongAddCommand());
            return response;
        }

        String phone = phoneAndName.get(0);
        String name = phoneAndName.get(1);

        phoneService.saveContact(phone, name, creator);
        response.setText(messageService.getAddSuccess());
        return response;
    }
}
