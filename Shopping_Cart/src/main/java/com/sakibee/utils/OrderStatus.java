package com.sakibee.utils;

public enum OrderStatus {
    IN_PROGRESS(1, "In Progress"),
    ORDER_RECIEVED(2, "Order Recieved"),
    PRODUCT_PACKED(3, "Product Packed"),
    OUT_FOR_DELIVERY(4, "Out For Delivery"),
    DELIVERED(5, "Delivered");



    private Integer id;
    private String name;

    OrderStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
