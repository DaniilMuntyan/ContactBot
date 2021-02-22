package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.service.commands.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.io.*;

@Service
public class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class);

    private final StatisticsService statisticsService;
    private final AddService addService;
    private final EditService editService;
    private final DeleteService deleteService;
    private final UnknownContactsService unknownContactsService;
    private final BackupService backupService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AdminService(StatisticsService statisticsService, AddService addService, EditService editService, DeleteService deleteService, UnknownContactsService unknownContactsService, BackupService backupService, AuthenticationService authenticationService) {
        LOGGER.info("AdminService is creating...");
        this.addService = addService;
        this.editService = editService;
        this.deleteService = deleteService;
        this.unknownContactsService = unknownContactsService;
        this.backupService = backupService;
        this.statisticsService = statisticsService;
        this.authenticationService = authenticationService;
    }

    public PartialBotApiMethod<?> handleAuthentication(String text, SendMessage response, User user) {
        return authenticationService.handleAuthentication(text, response, user);
    }

    public boolean checkAuthenticated(User user) {
        return user.isAdminMode();
    }

    public SendMessage addContact(String message, SendMessage response, User creator) {
        return addService.addContact(message, response, creator);
    }

    public SendMessage editContact(String message, SendMessage response, User editor) {
        return editService.editContact(message, response, editor);
    }

    public EditMessageText deleteContactConfirmed(String number, EditMessageText response) {
        return deleteService.deleteContactConfirmed(number, response);
    }

    public SendMessage deleteContactQuestion(String message, SendMessage response) {
        return deleteService.deleteContactQuestion(message, response);
    }

    public PartialBotApiMethod<?> unknownContacts(SendMessage response) throws IOException {
        return unknownContactsService.unknownContacts(response);
    }

    public PartialBotApiMethod<?> backup(SendMessage response) throws IOException {
        return backupService.backup(response);
    }

    public PartialBotApiMethod<?> stat(String message, SendMessage response) {
        return statisticsService.stat(message, response);
    }
}
