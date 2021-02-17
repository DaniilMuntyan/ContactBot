package com.example.demo.repo;

import com.example.demo.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    //@Query("SELECT n FROM Phone n WHERE n.phone = ?1")
    List<Phone> findAllByPhone(String phone);

    @Transactional
    @Modifying
    @Query("UPDATE Phone p SET p.name = ?2 WHERE p.phone = ?1")
    void editContact(String phone, String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM Phone p WHERE p.phone = ?1")
    void deleteByPhone(String phone);
}
