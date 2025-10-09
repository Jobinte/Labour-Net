package com.mini.labour_chain.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "admin")
@Data
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto-increment primary key
    private Long id;

    private String username;
    private String password;
    private String email;
}
