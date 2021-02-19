package com.example.demo.repo;

import com.example.demo.model.NewContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface NewContactRepository extends JpaRepository<NewContact, Long> {
    @Transactional
    @Query("SELECT n FROM NewContact n WHERE n.phone = ?1")
    Optional<NewContact> findByPhone(String phone);

    @Transactional
    Optional<NewContact> deleteByPhone(String phone);

    @Transactional
    @Modifying
    @Query(value="INSERT INTO \"new\" (phone) VALUES (?1) ON CONFLICT (phone) DO NOTHING", nativeQuery=true)
    void insertIfNotExist(String phone);

}
