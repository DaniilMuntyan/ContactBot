package com.example.demo.service;

import com.example.demo.model.Phone;
import com.example.demo.repo.NewContactRepository;
import com.example.demo.repo.PhoneRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public final class PhoneService {
    private static final Logger LOGGER = Logger.getLogger(PhoneService.class);

    private final PhoneRepository phoneRepository;
    private final NewContactRepository newContactRepository;

    public PhoneService(PhoneRepository phoneRepository, NewContactRepository newContactRepository) {
        LOGGER.info("PhoneService creating...");
        this.phoneRepository = phoneRepository;
        this.newContactRepository = newContactRepository;
    }

    public Phone saveContact(String phone, String name) {
        Phone newContact = Phone.builder().phone(phone).name(name).build();
        newContact = phoneRepository.save(newContact);
        LOGGER.info("Saved phone: " + newContact);
        if (newContactRepository.findByPhone(phone).isPresent()) {
            // Delete record from new contact, if its phone has been added to database
            newContactRepository.deleteByPhone(phone);
        }
        return phoneRepository.save(newContact);
    }

    public Phone saveContact(Contact contact) {
        Phone newContact = new Phone(contact);
        newContact = phoneRepository.save(newContact);
        LOGGER.info("Saved phone: " + newContact);
        return phoneRepository.save(newContact);
    }

    public List<Phone> findAllByPhone(String number) {
        return phoneRepository.findAllByPhone(number);
    }

    public boolean deleteContact(String number) {
        LOGGER.info("deleteContact NUMBER: " + number);

        phoneRepository.deleteByPhone(number);
        return true;
    }

    public String getPhoneFromMessage(String text) {
        short count = 0;
        StringBuilder phone = new StringBuilder();
        char c;
        for(int i = 0; i < text.length(); ++i) {
            c = text.charAt(i);
            if (c == '\"') {
                count++;
                continue;
            }
            if (count == 1) { // First quote occurrence
                if (Character.isLetter(c)) { // Phone number consists of only digits and '+' '-'
                    return null;
                }
                phone.append(c);
            }
        }
        LOGGER.info("getPhoneFromMessage: " + phone);

        // Supposed to be 2 quote symbols
        if (count != 2 || phone.toString().trim().isEmpty()) {
            return null;
        }
        return phone.toString().trim();
    }

    public List<Phone> findContact(String number) {
        return phoneRepository.findAllByPhone(number);
    }

    public boolean editContact(String phone, String name) {
        List<Phone> findPhone = phoneRepository.findAllByPhone(phone);
        if (findPhone.size() == 0) {
            return false;
        }
        /*for(Phone temp: findPhone) {
            temp.setName(name);
            phoneRepository.save(temp);
        }*/
        phoneRepository.editContact(phone, name);
        return true;
    }

    public List<Phone> getAllContacts() {
        return phoneRepository.findAll();
    }
}
