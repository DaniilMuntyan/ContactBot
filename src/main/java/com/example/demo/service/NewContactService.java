package com.example.demo.service;

import com.example.demo.model.UnknownPhone;
import com.example.demo.model.User;
import com.example.demo.repo.UnknownPhoneRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class NewContactService {
    private static final Logger LOGGER = Logger.getLogger(NewContactService.class);

    private final UnknownPhoneRepository unknownPhoneRepository;

    @Autowired
    public NewContactService(UnknownPhoneRepository unknownPhoneRepository) {
        LOGGER.info("NewContactService is creating...");
        this.unknownPhoneRepository = unknownPhoneRepository;
    }

    public void saveNewContact(String number, User user) {
        for(int i = 0; i < number.length(); ++i) {
            if (Character.isLetter(number.charAt(i))) {
                return;
            }
        }
        this.unknownPhoneRepository.insertIfNotExist(number, user.getId());
        LOGGER.info("Saved new contact: " + number);
    }

    public List<UnknownPhone> getAllNewContacts() {
        return unknownPhoneRepository.findAll();
    }
}
