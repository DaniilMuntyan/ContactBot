package com.example.demo.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user", schema="public")
@Entity
public final class User {
    @Id
    @SequenceGenerator(name = "USER_SEQUENCE", sequenceName = "user_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "USER_SEQUENCE")
    @Column(name="user_id")
    private Long id;

    @NotNull
    private Long chatId;

    @Column(name="firstname")
    private String firstName;

    @Column(name="lastname")
    private String lastName;

    @Column(name="username")
    private String username;

    @Column(name="created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name="last_action")
    @CreationTimestamp
    private Date lastAction;

    @Column(name="admin_mode")
    private boolean adminMode;

    public User(Long chatId, String firstName, String lastName, String username) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.adminMode = false;
    }

    public User(Message message) {
        this.chatId = message.getChatId();
        this.firstName = message.getFrom().getFirstName();
        this.lastName = message.getFrom().getLastName();
        this.username = message.getFrom().getUserName();
        this.adminMode = false;
    }

    public String getName() {
        String name = "";
        if (this.firstName != null) {
            name += this.firstName;
        }
        if (this.lastName != null) {
            name += " " + this.lastName;
        }
        if (this.username != null) {
            name += " (@" + this.username + ")";
        }
        return name;
    }
}
