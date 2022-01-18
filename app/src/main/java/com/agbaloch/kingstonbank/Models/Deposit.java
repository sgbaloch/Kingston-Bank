package com.agbaloch.kingstonbank.Models;

public class Deposit extends Transaction {

    private final int transactionType = 1;

    public int getTransactionType() {
        return transactionType;
    }
}
