package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.telegram.telegrambots.meta.api.objects.Contact;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="contacts", schema="public")
@Entity
public final class Phone {
    @Id
    @SequenceGenerator(name = "CONTACT_SEQUENCE", sequenceName = "contacts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "CONTACT_SEQUENCE")
    @Column(name="id")
    private Long id;

    @Column(name="phone")
    private String phone;

    @Column(name="name")
    private String name;

    @Column(name="init_date")
    @CreationTimestamp
    private Date creationDate;

    public Phone(Contact contact) {
        this.phone = contact.getPhoneNumber();
        this.name = "";
        this.name += contact.getFirstName() != null ? contact.getFirstName() + " " : "";
        this.name += contact.getLastName() != null ? contact.getLastName() + " " : "";
    }
}
