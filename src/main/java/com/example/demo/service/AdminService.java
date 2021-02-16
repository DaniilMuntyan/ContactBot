package com.example.demo.service;

import com.example.demo.botapi.ContactTelegramBot;
import com.example.demo.model.NewContact;
import com.example.demo.model.Phone;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public final class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class);

    private final MessageService messageService;
    private final PhoneService phoneService;
    private final NewContactService newContactService;

    @Value("${ADMIN_PASSWORD}")
    private String password;

    @Autowired
    public AdminService(MessageService messageService, PhoneService phoneService, NewContactService newContactService) {
        LOGGER.info("AdminService is creating...");
        this.newContactService = newContactService;
        this.phoneService = phoneService;
        this.messageService = messageService;
    }

    public String getPasswordFromMessage(String text) {
        String[] arrayString = text.split(" ");
        return arrayString[1].strip();
    }

    public boolean checkAuthentication(String text) {
        return this.password.strip().equals(text.strip());
    }

    private List<String> getPhoneAndName(String message) {
        int count = 0;
        StringBuilder phone = new StringBuilder();
        StringBuilder name = new StringBuilder();
        char c;
        for(int i = 0; i < message.length(); ++i) {
            c = message.charAt(i);
            if (c == '\"') {
                count++;
                continue;
            }
            switch (count) {
                case 1: // First quote occurrence
                    if (Character.isLetter(c)) { // Phone number consists of only digits and '+' '-'
                        return null;
                    }
                    phone.append(c);
                    break;
                case 3: // Third quote occurrence
                    name.append(c);
            }
        }
        LOGGER.info("getPhoneAndName: " + phone + " " + name);

        // Supposed to be 4 quote symbols
        if (count != 4 || phone.toString().strip().isEmpty() || name.toString().strip().isEmpty()) {
            return null;
        }

        return Arrays.asList(phone.toString(), name.toString());
    }

    public SendMessage addContact(String message, SendMessage response) {
        List<String> phoneAndName = getPhoneAndName(message);

        if(phoneAndName == null) {
            response.setText(messageService.getWrongAddCommand());
            return response;
        }

        String phone = phoneAndName.get(0);
        String name = phoneAndName.get(1);

        phoneService.saveContact(phone, name);
        response.setText(messageService.getAddSuccess());
        return response;
    }

    public SendMessage editContact(String message, SendMessage response) {
        List<String> phoneAndName = getPhoneAndName(message);
        if (phoneAndName == null) {
            response.setText(messageService.getEditFail());
            return response;
        }

        String phone = phoneAndName.get(0);
        String name = phoneAndName.get(1);

        Optional<Phone> editedPhone = phoneService.editContact(phone, name);
        if (editedPhone.isPresent()) {
            response.setText(messageService.getEditSuccess());
        } else {
            response.setText(messageService.getEditFail());
        }
        return response;
    }

    public SendMessage deleteContact(String message, SendMessage response) {
        if(message == null || message.strip().equals("")) {
            response.setText(messageService.getDeleteFail());
            return response;
        }
        LOGGER.info("deleteContact: " + message);
        Optional<Phone> deletedPhone = phoneService.deleteContact(message);
        if (deletedPhone.isEmpty()) {
            response.setText(messageService.getDeleteFail());
            return response;
        }
        response.setText(messageService.getDeleteSuccess());
        return response;
    }

    public PartialBotApiMethod<?> listNewContacts(SendMessage response) throws IOException {
        List<NewContact> newContacts = newContactService.getAllNewContacts();
        LOGGER.debug("listNewContacts: " + newContacts);
        if(newContacts.size() == 0) {
            return null;
        }
        StringBuilder answer = new StringBuilder();
        if (newContacts.size() < 10) {
            answer.append(messageService.getListText());
            for(NewContact temp: newContacts) {
                answer.append(temp.getId()).append(". ").append(temp.getPhone());
            }
            response.setText(answer.toString());
            return response;
        } else {
            File file = writeToFile(newContacts, "Unknown_phone_numbers.txt");
            if (file != null) {
                String chatId = response.getChatId();
                Integer messageId = response.getReplyToMessageId();
                return SendDocument.builder()
                        .chatId(chatId)
                        .replyToMessageId(messageId)
                        .caption(file.getName())
                        .document(new InputFile(file))
                        .build();
            } else {
                response.setText(messageService.getListFail());
                return response;
            }
        }
    }

    private File writeToFile(List<NewContact> newContacts, String filePath) {
        PrintWriter pw = null;
        File file = null;
        try {
            file = new File(filePath);
            pw = new PrintWriter(new FileOutputStream(file));
            for(NewContact temp: newContacts) {
                pw.write(temp.getId() + ". " + temp.getPhone() + "\n");
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
            ex.printStackTrace();
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
        }
        return file;
    }
}
