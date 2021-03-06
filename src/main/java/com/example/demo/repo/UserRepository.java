package com.example.demo.repo;

import com.example.demo.constants.SqlCommands;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long id);

    @Transactional
    @Modifying
    @Query(SqlCommands.userEditAdminMode)
    void editAdminMode(Long id, Boolean isAdmin);

    @Transactional
    @Modifying
    @Query(SqlCommands.userEditLastAction)
    void editLastAction(Long id, Date lastAction);
}
