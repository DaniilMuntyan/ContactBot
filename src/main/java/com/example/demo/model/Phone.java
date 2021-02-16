package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
