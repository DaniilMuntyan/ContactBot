package com.example.demo.repo;

import com.example.demo.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    @Query(value="SELECT n FROM Phone n WHERE n.phone = ?1")
    Optional<Phone> findByNumber(String number);

    @Modifying
    @Query("UPDATE Phone p SET p.name = ?2 WHERE p.phone = ?1")
    Optional<Phone> editContact(String phone, String name);

    Optional<Phone> deleteByPhone(String phone);
}
