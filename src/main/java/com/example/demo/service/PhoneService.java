package com.example.demo.service;

import com.example.demo.model.Phone;
import com.example.demo.model.User;
import com.example.demo.repo.PhoneRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;

import java.util.Optional;

@Service
public final class PhoneService {
    private static final Logger LOGGER = Logger.getLogger(PhoneService.class);

    private final PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Phone saveContact(String phone, String name) {
        Phone newContact = Phone.builder().phone(phone).name(name).build();
        newContact = phoneRepository.save(newContact);
        LOGGER.info("Saved phone: " + newContact);
        return phoneRepository.save(newContact);
    }

    public Phone saveContact(Contact contact) {
        Phone newContact = Phone.builder()
                .phone(contact.getPhoneNumber())
                .name(contact.getFirstName() + " " + contact.getLastName())
                .build();
        newContact = phoneRepository.save(newContact);
        LOGGER.info("Saved phone: " + newContact);
        return phoneRepository.save(newContact);
    }

    public Optional<Phone> deleteContact(String number) {
        Optional<Phone> deletedPhone = phoneRepository.deleteByPhone(number);
        LOGGER.info(deletedPhone.map(phone -> "Deleted phone: " + phone).orElse(""));
        return deletedPhone;
    }

    public Optional<Phone> findContact(String number) {
        return phoneRepository.findByNumber(number);
    }

    public Optional<Phone> editContact(String phone, String name) {
        Optional<Phone> editedPhone = phoneRepository.editContact(phone, name);
        LOGGER.info(editedPhone.map(value -> "Edited phone: " + value)
                .orElseGet(() -> String.format("Edited phone is empty: %s %s", phone, name)));
        return editedPhone;
    }
}
