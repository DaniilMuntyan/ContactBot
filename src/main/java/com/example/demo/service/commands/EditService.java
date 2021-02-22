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
public class EditService {
    private static final Logger LOGGER = Logger.getLogger(EditService.class);

    private final MessageService messageService;
    private final PhoneService phoneService;

    @Autowired
    public EditService(MessageService messageService, PhoneService phoneService) {
        LOGGER.info("EditService is creating...");
        this.messageService = messageService;
        this.phoneService = phoneService;
    }

    public SendMessage editContact(String message, SendMessage response, User editor) {
        List<String> phoneAndName = messageService.getPhoneAndName(message);
        if (phoneAndName == null) {
            response.setText(messageService.getEditFail());
            return response;
        }

        String phone = phoneAndName.get(0);
        String name = phoneAndName.get(1);

        boolean successEdit = phoneService.editContact(phone, name, editor);
        if (successEdit) {
            response.setText(messageService.getEditSuccess());
        } else {
            response.setText(messageService.getEditFail());
        }
        return response;
    }

}
