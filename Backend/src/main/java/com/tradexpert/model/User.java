package com.tradexpert.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradexpert.domain.USER_ROLE;
import com.tradexpert.domain.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;
    private String mobile;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private UserStatus status = UserStatus.PENDING;
    private boolean isVerified = false;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    private String picture;
    private USER_ROLE role = USER_ROLE.ROLE_USER;

    // New fields from the profile page
    private String dob;
    private String address;
    private String city;
    private String postcode;
    private String country;

    // You might want to add these annotations for database columns
    /*
    @Column(nullable = true) // Makes the column nullable in database
    private String dob;

    @Column(nullable = true)
    private String nationality;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String postcode;

    @Column(nullable = true)
    private String country;
    */
}