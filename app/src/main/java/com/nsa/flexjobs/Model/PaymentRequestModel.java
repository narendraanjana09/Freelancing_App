package com.nsa.flexjobs.Model;

public class PaymentRequestModel {
    private String id;
    private String amount;
    private String date;
    private String paymentBy;
    private String number;

    public PaymentRequestModel(String id, String amount, String date, String paymentBy, String number) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.paymentBy = paymentBy;
        this.number = number;
    }

    public PaymentRequestModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymentBy() {
        return paymentBy;
    }

    public void setPaymentBy(String paymentBy) {
        this.paymentBy = paymentBy;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
