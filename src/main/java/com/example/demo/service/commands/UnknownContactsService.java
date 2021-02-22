package com.example.demo.service.commands;

import com.example.demo.constants.ProgramVariables;
import com.example.demo.model.UnknownPhone;
import com.example.demo.service.MessageService;
import com.example.demo.service.model.NewContactService;
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
public class UnknownContactsService {
    private static final Logger LOGGER = Logger.getLogger(UnknownContactsService.class);

    private final NewContactService newContactService;
    private final MessageService messageService;
    private final ProgramVariables programVariables;
    private final SimpleDateFormat simpleDateFormat;

    public UnknownContactsService(NewContactService newContactService, MessageService messageService, ProgramVariables programVariables) {
        LOGGER.info("UnknownContactsService is creating...");
        this.newContactService = newContactService;
        this.messageService = messageService;
        this.programVariables = programVariables;
        this.simpleDateFormat = new SimpleDateFormat(programVariables.getDateFormat());
    }

    public PartialBotApiMethod<?> unknownContacts(SendMessage response) throws IOException {
        List<UnknownPhone> unknownPhones = newContactService.getAllNewContacts();
        if(unknownPhones.size() == 0) {
            response.setText(messageService.getListNoRecords());
            return response;
        }
        StringBuilder answer = new StringBuilder();
        if (unknownPhones.size() < 10) {
            answer.append(messageService.getListText());
            for(UnknownPhone temp: unknownPhones) {
                answer.append(temp.getPhone()).append("\n");
            }
            response.setText(answer.toString());
            return response;
        } else {
            File file = this.writeUnknownToFile(unknownPhones);
            if (file != null) {
                String chatId = response.getChatId();
                Integer messageId = response.getReplyToMessageId();
                return SendDocument.builder()
                        .chatId(chatId)
                        .replyToMessageId(messageId)
                        .caption((unknownPhones.size() - 1) + " " + programVariables.getUnknownNumbersMessage())
                        .document(new InputFile(file, programVariables.getUnknownNumbersFileName()))
                        .build();
            } else {
                response.setText(messageService.getListFail());
                return response;
            }
        }
    }

    public File writeUnknownToFile(List<UnknownPhone> unknownPhones) throws IOException {
        File file = new File(String.format(programVariables.getUnknownNumbersFilePath(), Thread.currentThread().getName()));
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        List<String[]> contacts = this.createCsvDataNewContacts(unknownPhones);
        try (FileWriter fw = new FileWriter(file); CSVWriter writer = new CSVWriter(fw)) {
            writer.writeAll(contacts);
        } catch (IOException e) {
            LOGGER.error(e);
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private List<String[]> createCsvDataNewContacts(List<UnknownPhone> unknownPhones) {
        String[] header = {"id", "phone", "created at", "created by"};
        List<String[]> list = new ArrayList<>();
        list.add(header);
        for(UnknownPhone temp: unknownPhones) {
            list.add(new String[] {temp.getId().toString().trim(), temp.getPhone().trim(),
                    simpleDateFormat.format(temp.getCreatedAt()), temp.getCreator().getName()});
        }
        return list;
    }
}
