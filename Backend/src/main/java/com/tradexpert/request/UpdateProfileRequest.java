package com.tradexpert.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String mobile;
    private String dob;
    private String nationality;
    private String address;
    private String city;
    private String postcode;
    private String country;
    private String picture;

    // Getters and setters for all fields
    // Constructor if needed
}