package com.example.demo.service.commands;

import com.example.demo.constants.ProgramVariables;
import com.example.demo.model.Phone;
import com.example.demo.service.MessageService;
import com.example.demo.service.model.PhoneService;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class BackupService {
    private static final Logger LOGGER = Logger.getLogger(BackupService.class);

    private final MessageService messageService;
    private final PhoneService phoneService;
    private final ProgramVariables programVariables;
    private final SimpleDateFormat simpleDateFormat;

    public BackupService(MessageService messageService, PhoneService phoneService, ProgramVariables programVariables) {
        LOGGER.info("BackupService is creating...");
        this.messageService = messageService;
        this.phoneService = phoneService;
        this.programVariables = programVariables;
        this.simpleDateFormat = new SimpleDateFormat(programVariables.getDateFormat());
    }

    public PartialBotApiMethod<?> backup(SendMessage response) throws IOException {
        List<String[]> phones = createCsvDataPhones(phoneService.getAllContacts());
        if (phones.size() == 0) {
            response.setText(messageService.getBackupNoRecords());
            return response;
        }
        File file = new File(String.format(programVariables.getBackupFilePath(), Thread.currentThread().getName()));
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
                .document(new InputFile(file, programVariables.getBackupFileName()))
                .build();
    }

    private List<String[]> createCsvDataPhones(List<Phone> phones) {
        String[] header = {"id", "phone", "name", "created at", "updated at",
                "created by", "updated by"};
        List<String[]> list = new ArrayList<>();
        list.add(header);
        for(Phone temp: phones) {
            list.add(new String[] {temp.getId().toString().trim(), temp.getPhone().trim(), temp.getName().trim(),
                    simpleDateFormat.format(temp.getCreatedAt()), simpleDateFormat.format(temp.getUpdatedAt()),
                    temp.getCreator().getName(), temp.getEditor().getName()});
        }
        return list;
    }
}
