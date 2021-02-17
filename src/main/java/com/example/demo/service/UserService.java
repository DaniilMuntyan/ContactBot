package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Date;
import java.util.Optional;

@Service
public final class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        LOGGER.info("UserService is creating...");
        this.userRepository = userRepository;
    }

    public Optional<User> findUserByFirstname(String firstname) {
        return userRepository.findByFirstname(firstname);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByChatId(Long id) {
        return this.userRepository.findByChatId(id);
    }

    public User saveUser(User newUser) {
        User savedUser = this.userRepository.save(newUser);
        LOGGER.info(String.format("saveUser: %s", savedUser));
        return savedUser;
    }

    public void editAdminMode(User user, boolean isAdmin) {
        userRepository.editAdminMode(user.getId(), isAdmin);
    }

    public void editLastAction(User user, Date lastAction) {
        userRepository.editLastAction(user.getId(), lastAction);
    }

    public boolean checkDdos(User user) { // True, if ddos
        Date now = new Date(System.currentTimeMillis());
        long secondsBetween = (now.getTime() - user.getLastAction().getTime());
        if (secondsBetween < 1000) {
            return true;
        }

        user.setLastAction(now);
        this.editLastAction(user, now);

        return false;
    }

    public User getUserFromMessage(Message message) {
        return new User(message);
    }
}