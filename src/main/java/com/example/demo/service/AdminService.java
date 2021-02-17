package com.example.demo.service;

import com.example.demo.constants.ProgramVariables;
import com.example.demo.model.NewContact;
import com.example.demo.model.Phone;
import com.example.demo.model.User;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public final class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class);

    private final MessageService messageService;
    private final PhoneService phoneService;
    private final NewContactService newContactService;
    private final ProgramVariables programVariables;

    private final SimpleDateFormat simpleDateFormat;

    @Value("${ADMIN_PASSWORD}")
    private String password;

    @Autowired
    public AdminService(MessageService messageService, PhoneService phoneService, NewContactService newContactService, ProgramVariables programVariables) {
        LOGGER.info("AdminService is creating...");
        this.newContactService = newContactService;
        this.phoneService = phoneService;
        this.messageService = messageService;
        this.programVariables = programVariables;
        this.simpleDateFormat = new SimpleDateFormat(programVariables.getDateFormat());
    }

    public String getPasswordFromMessage(String text) {
        String[] arrayString = text.split(" ");
        return arrayString[1].strip();
    }

    public boolean checkAuthentication(User user) {
        return user.isAdminMode();
    }

    public boolean checkAuthentication(String text) {
        return this.password.strip().equals(text.strip());
    }

    private List<String> getPhoneAndName(String message) {
        LOGGER.info("getPhoneAndName: " + message);
        if (message.length() == 0 || message.charAt(0) != '\"' || message.charAt(message.length() - 1) != '\"') {
            return null;
        }
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

        boolean successEdit = phoneService.editContact(phone, name);
        if (successEdit) {
            response.setText(messageService.getEditSuccess());
        } else {
            response.setText(messageService.getEditFail());
        }
        return response;
    }

    private SendMessage deleteFailed(SendMessage response) {
        response.setText(messageService.getDeleteFail());
        return response;
    }

    private EditMessageText deleteFailed(EditMessageText response) {
        response.setText(messageService.getDeleteFail());
        return response;
    }

    public EditMessageText deleteContactConfirmed(String number, EditMessageText response) {
        if(isInvalidDeleteMessage(number)) {
            return deleteFailed(response);
        }

        List<Phone> findPhone = phoneService.findAllByPhone(number); // Look for the number
        if (findPhone.size() == 0) { // If it doesn't exist - we can't delete it
            return deleteFailed(response);
        }

        boolean deleteSuccess = phoneService.deleteContact(number);
        if (!deleteSuccess) {
            response.setText(messageService.getDeleteFail());
            return response;
        }
        response.setText(messageService.getDeleteSuccess());
        return response;

    }

    public PartialBotApiMethod<?> deleteContactQuestion(String message, SendMessage response) {
        if(isInvalidDeleteMessage(message)) {
            return deleteFailed(response);
        }

        String number = phoneService.getPhoneFromMessage(message);
        List<Phone> findPhones = phoneService.findAllByPhone(number); // Look for the number
        if (findPhones.size() == 0) { // If it doesn't exist - we can't delete it
            return deleteFailed(response);
        }

        StringBuilder textMessage = new StringBuilder();
        for(Phone temp: findPhones) {
            textMessage.append("\"").append(temp.getPhone().strip()).append("\"")
                    .append(" \"").append(temp.getName().strip()).append("\"").append("\n");
        }
        textMessage.delete(textMessage.length() - 1, textMessage.length());

        response.setText(String.format(messageService.getDeleteAcknowledge(), textMessage.toString()));
        response.setReplyMarkup(getDeleteKeyboard(number));

        return response;

        /*boolean deleteSuccess = phoneService.deleteContact(message);
        if (!deleteSuccess) {
            response.setText(messageService.getDeleteFail());
            return response;
        }
        response.setText(messageService.getDeleteSuccess());
        return response;*/
    }

    private boolean isInvalidDeleteMessage(String message) {
        return message == null || message.strip().equals("");
    }

    private InlineKeyboardMarkup getDeleteKeyboard(String number) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonYes = InlineKeyboardButton
                .builder()
                .text("Yes")
                .callbackData(programVariables.getDeleteCallbackYes() + " " + number)
                .build();
        InlineKeyboardButton buttonNo = InlineKeyboardButton
                .builder()
                .text("No")
                .callbackData(programVariables.getDeleteCallbackNo())
                .build();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(new ArrayList<>(List.of(buttonYes, buttonNo)));

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public PartialBotApiMethod<?> listNewContacts(SendMessage response) throws IOException {
        List<NewContact> newContacts = newContactService.getAllNewContacts();
        LOGGER.debug("listNewContacts: " + newContacts);
        if(newContacts.size() == 0) {
            response.setText(messageService.getListNoRecords());
            return response;
        }
        StringBuilder answer = new StringBuilder();
        if (newContacts.size() < 10) {
            answer.append(messageService.getListText());
            for(NewContact temp: newContacts) {
                answer.append(temp.getPhone()).append("\n");
            }
            response.setText(answer.toString());
            return response;
        } else {
            File file = writeToFile(newContacts);
            if (file != null) {
                String chatId = response.getChatId();
                Integer messageId = response.getReplyToMessageId();
                return SendDocument.builder()
                        .chatId(chatId)
                        .replyToMessageId(messageId)
                        .caption((newContacts.size() - 1) + " " + programVariables.getUnknownNumbersMsgText())
                        .document(new InputFile(file))
                        .build();
            } else {
                response.setText(messageService.getListFail());
                return response;
            }
        }
    }

    private File writeToFile(List<NewContact> newContacts) throws IOException {
        File file = new File(programVariables.getUnknownNumbersFilePath());
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        List<String[]> contacts = createCsvDataNewContacts(newContacts);
        try (FileWriter fw = new FileWriter(file); CSVWriter writer = new CSVWriter(fw)) {
            writer.writeAll(contacts);
        } catch (IOException e) {
            LOGGER.error(e);
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public PartialBotApiMethod<?> backup(SendMessage response) throws IOException {
        List<String[]> phones = createCsvDataPhones(phoneService.getAllContacts());
        if (phones.size() == 0) {
            response.setText(messageService.getBackupNoRecords());
            return response;
        }
        File file = new File(programVariables.getBackupFilePath());
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        try (FileWriter fw = new FileWriter(file); CSVWriter writer = new CSVWriter(fw)) {
            writer.writeAll(phones);
        } catch (IOException e) {
            LOGGER.error(e);
            e.printStackTrace();
            response.setText(programVariables.getBackupFail());
            return response;
        }
        String chatId = response.getChatId();
        Integer messageId = response.getReplyToMessageId();
        return SendDocument.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .caption(String.format(programVariables.getBackupCaption(), "" + (phones.size() - 1)))
                .document(new InputFile(file))
                .build();
    }

    private List<String[]> createCsvDataPhones(List<Phone> phones) {
        String[] header = {"id", "phone", "name", "creation date"};
        List<String[]> list = new ArrayList<>();
        list.add(header);
        for(Phone temp: phones) {
            list.add(new String[] {temp.getId().toString().strip(), temp.getName().strip(), temp.getPhone().strip(),
                    simpleDateFormat.format(temp.getCreationDate())});
        }
        return list;
    }

    private List<String[]> createCsvDataNewContacts(List<NewContact> newContacts) {
        String[] header = {"id", "phone", "date"};
        List<String[]> list = new ArrayList<>();
        list.add(header);
        for(NewContact temp: newContacts) {
            list.add(new String[] {temp.getId().toString().strip(), temp.getPhone().strip(),
                    simpleDateFormat.format(temp.getInitDate())});
        }
        return list;
    }

}
