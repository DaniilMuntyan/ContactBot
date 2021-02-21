package com.example.demo.service.commands;

import com.example.demo.constants.ProgramVariables;
import com.example.demo.model.Phone;
import com.example.demo.model.User;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class StatisticsService {
    private static final Logger LOGGER = Logger.getLogger(StatisticsService.class);

    private final PhoneService phoneService;
    private final ProgramVariables programVariables;
    private final SimpleDateFormat simpleDateFormat;
    private final MessageService messageService;

    public StatisticsService(PhoneService phoneService, ProgramVariables programVariables, MessageService messageService) {
        LOGGER.info("StatisticsService is creating...");
        this.phoneService = phoneService;
        this.programVariables = programVariables;
        this.messageService = messageService;
        this.simpleDateFormat = new SimpleDateFormat(this.programVariables.getDateFormat());
    }

    public PartialBotApiMethod<?> stat(String message, SendMessage response) {
        if(!message.isEmpty()) {
            return statFailed(response);
        }
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date before = cal.getTime();
        List<Phone> allPhonesLastDays = phoneService.stat(before, today);
        try {
            File csvData = writeStatCsv(allPhonesLastDays);
            if (csvData != null) {
                String chatId = response.getChatId();
                Integer messageId = response.getReplyToMessageId();
                return SendDocument.builder()
                        .chatId(chatId)
                        .replyToMessageId(messageId)
                        .document(new InputFile(csvData, programVariables.getStatFileName()))
                        .caption(programVariables.getStatFileCaption())
                        .build();
            }
        } catch (IOException e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
        return statFailed(response);
    }

    private SendMessage statFailed(SendMessage response) {
        response.setText(messageService.getAdminStatFail());
        return response;
    }

    public File writeStatCsv(List<Phone> allPhonesLastDays) throws IOException {
        File file = new File(String.format(programVariables.getStatFilePath(), Thread.currentThread().getName()));
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        String[] header = {"creator user id", "firstname", "lastname", "username", "created at", "phone", "name"};
        List<String[]> csvPhones = new ArrayList<>();
        csvPhones.add(header);
        for(Phone phone: allPhonesLastDays) {
            User creator = phone.getCreator();
            csvPhones.add(new String[] {creator.getId().toString(), creator.getFirstName(), creator.getLastName(),
                    creator.getUsername(), simpleDateFormat.format(phone.getCreatedAt()), phone.getPhone().trim(),
                    phone.getName()});
        }

        try (FileWriter fw = new FileWriter(file); CSVWriter writer = new CSVWriter(fw)) {
            writer.writeAll(csvPhones);
        } catch (IOException e) {
            LOGGER.error(e);
            e.printStackTrace();
            return null;
        }
        return file;
    }
}
