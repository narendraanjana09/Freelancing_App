package com.nsa.labelimages.Model;

public class BalanceModel {
    private String balance;
    private String inQueue;
    private String credited;

    public BalanceModel() {
    }

    public BalanceModel(String balance, String inQueue, String credited) {
        this.balance = balance;
        this.inQueue = inQueue;
        this.credited = credited;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getInQueue() {
        return inQueue;
    }

    public void setInQueue(String inQueue) {
        this.inQueue = inQueue;
    }

    public String getCredited() {
        return credited;
    }

    public void setCredited(String credited) {
        this.credited = credited;
    }
}
