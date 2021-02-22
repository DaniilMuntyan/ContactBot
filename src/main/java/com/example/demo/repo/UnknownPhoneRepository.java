package com.example.demo.repo;

import com.example.demo.constants.SqlCommands;
import com.example.demo.model.UnknownPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface UnknownPhoneRepository extends JpaRepository<UnknownPhone, Long> {
    @Query(SqlCommands.unknownFindByPhone)
    Optional<UnknownPhone> findByPhone(String phone);

    @Transactional
    Optional<UnknownPhone> deleteByPhone(String phone);

    @Transactional
    @Modifying
    @Query(value=SqlCommands.unknownInsertIfNotExist, nativeQuery=true)
    void insertIfNotExist(String phone, Long creatorId);

}
