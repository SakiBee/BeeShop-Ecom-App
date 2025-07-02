package com.sakibee.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRequest {

    private String fullName;

    private String email;

    private String mobileNo;

    private String address;

    private String city;

    private String postCode;

    private String paymentType;

}
