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
@Table(name="new", schema="public")
@Entity
public final class NewContact {
    @Id
    @SequenceGenerator(name = "NEW_SEQUENCE", sequenceName = "new_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "NEW_SEQUENCE")
    @Column(name="id")
    private Long id;

    @Column(name="phone")
    private String phone;
}
