package com.example.demo.repo;

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

    @Query("SELECT u FROM User u WHERE u.firstName = ?1")
    Optional<User> findByFirstname(String firstname);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.adminMode = ?2 WHERE u.id = ?1")
    void editAdminMode(Long id, Boolean isAdmin);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.lastAction = ?2 WHERE u.id = ?1")
    void editLastAction(Long id, Date lastAction);
}
