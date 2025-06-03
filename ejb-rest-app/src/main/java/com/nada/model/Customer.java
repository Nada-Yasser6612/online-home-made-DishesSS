package com.nada.model;

public class Customer extends User {
    private int customerId;
    private String cardNumber;
    private String expireDate;

    public Customer() {}

    public Customer(int customerId, String cardNumber, String expireDate,
                    int userId, String username, String email, String password,
                    boolean isAdmin, String address, String phone) {
        super(email, password, username, isAdmin, address, phone);
        this.customerId = customerId;
        this.cardNumber = cardNumber;
        this.expireDate = expireDate;
        this.setId(userId); // inherited from User
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
