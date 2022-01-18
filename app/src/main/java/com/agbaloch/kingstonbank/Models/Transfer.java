package com.agbaloch.kingstonbank.Models;

public class Transfer extends Transaction {

    private final int transactionType = 3;

    private long targetAccount;

    public int getTransactionType() {
        return transactionType;
    }

    public long getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(long targetAccount) {
        this.targetAccount = targetAccount;
    }
}
