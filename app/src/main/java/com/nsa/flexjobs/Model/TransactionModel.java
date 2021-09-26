package com.nsa.flexjobs.Model;

public class TransactionModel {
    private String status;
    private String amount;
    private String date;
    private String transactionID;
    private String transactionmessage;
    private String transactionNote;

    public TransactionModel() {
    }

    public TransactionModel(String status, String amount, String date, String transactionID, String transactionmessage, String transactionNote) {
        this.status = status;
        this.amount = amount;
        this.date = date;
        this.transactionID = transactionID;
        this.transactionmessage = transactionmessage;
        this.transactionNote = transactionNote;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionmessage() {
        return transactionmessage;
    }

    public void setTransactionmessage(String transactionmessage) {
        this.transactionmessage = transactionmessage;
    }

    public String getTransactionNote() {
        return transactionNote;
    }

    public void setTransactionNote(String transactionNote) {
        this.transactionNote = transactionNote;
    }
}
