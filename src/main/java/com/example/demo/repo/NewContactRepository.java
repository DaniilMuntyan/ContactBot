package com.example.demo.repo;

import com.example.demo.model.NewContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NewContactRepository extends JpaRepository<NewContact, Long> {

}
