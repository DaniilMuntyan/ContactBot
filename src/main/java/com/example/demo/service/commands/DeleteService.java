package com.example.demo.service.commands;

import com.example.demo.constants.ProgramVariables;
import com.example.demo.model.Phone;
import com.example.demo.service.MessageService;
import com.example.demo.service.model.PhoneService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeleteService {
    private static final Logger LOGGER = Logger.getLogger(DeleteService.class);

    private final MessageService messageService;
    private final PhoneService phoneService;
    private final ProgramVariables programVariables;

    @Autowired
    public DeleteService(MessageService messageService, PhoneService phoneService, ProgramVariables programVariables) {
        LOGGER.info("DeleteService is creating...");
        this.messageService = messageService;
        this.phoneService = phoneService;
        this.programVariables = programVariables;
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

    public SendMessage deleteContactQuestion(String message, SendMessage response) {
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
            textMessage.append("\"").append(temp.getPhone().trim()).append("\"")
                    .append(" \"").append(temp.getName().trim()).append("\"").append("\n");
        }
        textMessage.delete(textMessage.length() - 1, textMessage.length());

        response.setText(String.format(messageService.getDeleteAcknowledge(), textMessage.toString()));
        response.setReplyMarkup(getDeleteKeyboard(number));

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

    private boolean isInvalidDeleteMessage(String message) {
        return message == null || message.trim().equals("");
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
}
