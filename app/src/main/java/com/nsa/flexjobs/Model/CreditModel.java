package com.nsa.flexjobs.Model;

public class CreditModel {
    private String id;
    private String date;
    private String amount;
    private String number;
    private String paymentBy;
    private String paymetRef;

    public CreditModel() {
    }

    public CreditModel(String id, String date, String amount, String number, String paymentBy, String paymetRef) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.number = number;
        this.paymentBy = paymentBy;
        this.paymetRef = paymetRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPaymentBy() {
        return paymentBy;
    }

    public void setPaymentBy(String paymentBy) {
        this.paymentBy = paymentBy;
    }

    public String getPaymetRef() {
        return paymetRef;
    }

    public void setPaymetRef(String paymetRef) {
        this.paymetRef = paymetRef;
    }
}
