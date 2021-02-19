package com.example.demo.repo;

import com.example.demo.constants.SqlCommands;
import com.example.demo.model.Phone;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    List<Phone> findAllByPhone(String phone);

    @Transactional
    @Modifying
    @Query(SqlCommands.phoneEditContact)
    void editContact(String phone, String name, User editor);

    @Transactional
    @Modifying
    @Query(SqlCommands.phoneDeleteByPhone)
    void deleteByPhone(String phone);
}
