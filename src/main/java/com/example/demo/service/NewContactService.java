package com.example.demo.service;

import com.example.demo.model.NewContact;
import com.example.demo.model.User;
import com.example.demo.repo.NewContactRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

@Service
public final class NewContactService {
    private static final Logger LOGGER = Logger.getLogger(NewContactService.class);

    private final NewContactRepository newContactRepository;

    @Autowired
    public NewContactService(NewContactRepository newContactRepository) {
        LOGGER.info("NewContactService is creating...");
        this.newContactRepository = newContactRepository;
    }

    public void saveNewContact(String number) {
        for(int i = 0; i < number.length(); ++i) {
            if (Character.isLetter(number.charAt(i))) {
                return;
            }
        }
        NewContact newContact = NewContact.builder().phone(number).build();
        NewContact savedContact = this.newContactRepository.save(newContact);
        LOGGER.info("Saved new contact: " + savedContact);
    }

    public List<NewContact> getAllNewContacts() {
        return newContactRepository.findAll();
    }
}
