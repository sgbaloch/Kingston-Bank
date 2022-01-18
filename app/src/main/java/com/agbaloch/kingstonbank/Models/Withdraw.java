package com.agbaloch.kingstonbank.Models;

public class Withdraw extends Transaction {

    private final int transactionType = 2;

    public int getTransactionType() {
        return transactionType;
    }
}
