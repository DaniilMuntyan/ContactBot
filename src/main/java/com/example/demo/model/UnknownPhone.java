package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="new", schema="public")
@Entity
public final class UnknownPhone {
    @Id
    @SequenceGenerator(name = "NEW_SEQUENCE", sequenceName = "new_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "NEW_SEQUENCE")
    @Column(name="id")
    private Long id;

    @Column(name="phone")
    private String phone;

    @Column(name="created_at")
    @CreationTimestamp
    private Date createdAt;

    @ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name = "created_by")
    private User creator;
}
