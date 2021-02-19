package com.example.demo.constants;

public interface SqlCommands {
    String unknownFindByPhone = "SELECT n FROM UnknownPhone n WHERE n.phone = ?1";
    String unknownInsertIfNotExist = "INSERT INTO \"new\" (phone, created_by) VALUES (?1, ?2) ON CONFLICT (phone) DO NOTHING";

    String phoneEditContact = "UPDATE Phone p SET p.name = ?2, p.editor = ?3, p.updatedAt = current_timestamp WHERE p.phone = ?1";
    String phoneDeleteByPhone = "DELETE FROM Phone p WHERE p.phone = ?1";

    String userEditAdminMode = "UPDATE User u SET u.adminMode = ?2 WHERE u.id = ?1";
    String userEditLastAction = "UPDATE User u SET u.lastAction = ?2 WHERE u.id = ?1";
}
