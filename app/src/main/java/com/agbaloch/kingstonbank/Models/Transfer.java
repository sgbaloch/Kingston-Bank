package com.agbaloch.kingstonbank.Models;

public class Transfer extends Transaction {

    private final int transactionType = 3;

    private int targetAccount;

    public int getTransactionType() {
        return transactionType;
    }

    public int getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(int targetAccount) {
        this.targetAccount = targetAccount;
    }
}
