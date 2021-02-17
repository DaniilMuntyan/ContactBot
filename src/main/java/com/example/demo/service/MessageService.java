package com.example.demo.service;

import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:messages.properties")
public class MessageService {
    private static final Logger LOGGER = Logger.getLogger(MessageService.class);

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

    public MessageService() {
        LOGGER.info("MessageService is creating...");
    }

    public int countWords(String text) {
        String[] arrayString = text.split(" ");
        return arrayString.length;
    }
}
