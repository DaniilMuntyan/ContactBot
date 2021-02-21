package com.example.demo.service;

import com.example.demo.constants.ProgramVariables;
import com.opencsv.CSVWriter;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@PropertySource("classpath:messages.properties")
public class MessageService {
    private static final Logger LOGGER = Logger.getLogger(MessageService.class);
    private final ProgramVariables programVariables;

    @Value("${message.help}")
    @Getter
    private String helpMessage;

    @Value("${message.start}")
    @Getter
    private String startMessage;

    @Value("${message.numberNotFound}")
    @Getter
    private String notFoundMessage;

    @Value("${message.admin.wrongAdminPassword}")
    @Getter
    private String wrongAdminPassword;

    @Value("${message.admin.helloAdmin}")
    @Getter
    private String helloAdmin;

    @Value("${message.admin.add.fail}")
    @Getter
    private String wrongAddCommand;

    @Value("${message.admin.add.success}")
    @Getter
    private String addSuccess;

    @Value("${message.admin.add.fail}")
    @Getter
    private String addFail;

    @Value("${message.admin.edit.success}")
    @Getter
    private String editSuccess;

    @Value("${message.admin.edit.fail}")
    @Getter
    private String editFail;

    @Value("${message.admin.edit.noSuchNumber}")
    @Getter
    private String editNoNumber;

    @Value("${message.admin.delete.success}")
    @Getter
    private String deleteSuccess;

    @Value("${message.admin.delete.fail}")
    @Getter
    private String deleteFail;

    @Value("${message.admin.list.noRecords}")
    @Getter
    private String listNoRecords;

    @Value("${message.admin.list.text}")
    @Getter
    private String listText;

    @Value("${message.admin.alreadyInAdminMode}")
    @Getter
    private String alreadyAdmin;

    @Value("${message.admin.list.fail}")
    @Getter
    private String listFail;

    @Value("${message.admin.backup.noRecords}")
    @Getter
    private String backupNoRecords;

    @Value("${message.user.phone.text}")
    @Getter
    private String userPhoneText;

    @Value("${message.admin.delete.acknowledge}")
    @Getter
    private String deleteAcknowledge;

    @Value("${message.admin.delete.cancel}")
    @Getter
    private String deleteCancel;

    @Value("${message.admin.notAllowed}")
    @Getter
    private String adminNotAllowed;

    @Value("${message.admin.stat.fail}")
    @Getter
    private String adminStatFail;

    public MessageService(ProgramVariables programVariables) {
        LOGGER.info("MessageService is creating...");
        this.programVariables = programVariables;
    }

    public int countWords(String text) {
        String[] arrayString = text.split(" ");
        return arrayString.length;
    }

    public SendMessage getYouAreNotAdmin(SendMessage response) {
        response.setText(adminNotAllowed);
        return response;
    }

    public SendDocument getFileWithText(String text, Update update) throws IOException {
        File file = new File(programVariables.getTextMessageFilePath());
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(text);
        }

        return SendDocument
                .builder()
                .chatId(update.getMessage().getChatId().toString())
                .replyToMessageId(update.getMessage().getMessageId())
                .document(new InputFile(file, programVariables.getTextMessageFileName()))
                .caption(programVariables.getTextMessageFileCaption())
                .build();
    }

    public List<String> getPhoneAndName(String message) {
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

        if (count != 4 || phone.toString().trim().isEmpty() || name.toString().trim().isEmpty()) {
            return null; // Supposed to be 4 quote symbols
        }

        return Arrays.asList(phone.toString(), name.toString());
    }
}
