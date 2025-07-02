package com.sakibee.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data

public class OrderAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    private Integer Id;
    private String fullName;

    private String email;

    private String mobileNo;

    private String address;

    private String city;

    private String postCode;
}
